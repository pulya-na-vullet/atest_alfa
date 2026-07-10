package tests.postman;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Epic("Страхование Учет")
@Feature("Postman parity")
@Story("Покрытие Java-тестами всех запросов из Postman-коллекции")
public class PostmanCollectionParityTests {

    @Test
    @DisplayName("Insurance API Postman collection parity")
    void postmanCollectionParity() throws IOException {
        PostmanCollectionRunner runner = new PostmanCollectionRunner(
                Path.of("postman/insurance-api-tests.collection.json"),
                Path.of("postman/insurance-api-tests.environment.test.json")
        );
        runner.executeAll();
    }

    static class PostmanCollectionRunner {

        private static final Pattern STATUS_PATTERN = Pattern.compile("pm\\.response\\.to\\.have\\.status\\((\\d+)]?\\);");
        private static final Pattern STATUS_FROM_NAME_PATTERN = Pattern.compile("-\\s*(\\d{3})\\b");
        private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");
        private static final Pattern ENV_SET_PATTERN = Pattern.compile(
                "pm\\.(?:environment|variables)\\.set\\(\"([^\"]+)\",\\s*(.+)\\);"
        );
        private static final Pattern SIMPLE_JSON_DATA_SET_PATTERN = Pattern.compile(
                "jsonData\\.([A-Za-z0-9_.$\\[\\]]+)"
        );
        private static final Pattern SIMPLE_REQUEST_BODY_SET_PATTERN = Pattern.compile(
                "requestBody\\.([A-Za-z0-9_.$\\[\\]]+)"
        );

        private final ObjectMapper mapper = new ObjectMapper();
        private final Map<String, String> variables = new LinkedHashMap<>();
        private final List<PostmanRequest> requests = new ArrayList<>();

        PostmanCollectionRunner(Path collectionPath, Path environmentPath) throws IOException {
            JsonNode collection = mapper.readTree(collectionPath.toFile());
            JsonNode environment = mapper.readTree(environmentPath.toFile());
            loadEnvironmentVariables(environment);
            loadCollectionVariables(collection);
            flattenRequests(collection.path("item"), "", requests);
        }

        void executeAll() {
            for (PostmanRequest request : requests) {
                executeRequest(request);
            }
        }

        private void executeRequest(PostmanRequest request) {
            runPreRequestScript(request.preRequestLines);

            String resolvedUrl = resolveTemplate(request.rawUrl);
            ResolvedBody resolvedBody = resolveBody(request.bodyMode, request.rawBody, request.urlencodedItems);

            io.restassured.specification.RequestSpecification spec = RestAssured
                    .given()
                    .relaxedHTTPSValidation()
                    .log().ifValidationFails();

            for (PostmanHeader header : request.headers) {
                if (!header.enabled || header.key == null || header.value == null) {
                    continue;
                }
                spec.header(header.key, resolveTemplate(header.value));
            }

            if ("urlencoded".equals(request.bodyMode)) {
                for (Map.Entry<String, String> formItem : resolvedBody.urlencodedBody.entrySet()) {
                    spec.formParam(formItem.getKey(), formItem.getValue());
                }
            } else if (resolvedBody.rawBody != null) {
                spec.body(resolvedBody.rawBody);
            }

            Response response = spec.request(request.method, resolvedUrl);
            int expectedStatus = extractExpectedStatus(request);
            assertEquals(
                    expectedStatus,
                    response.statusCode(),
                    "Unexpected HTTP status for request: " + request.fullName
            );

            JsonNode responseJson = tryParseJson(response);
            runBasicPostmanAssertions(request.testLines, response, responseJson);
            extractAndPersistVariables(request.testLines, responseJson, resolvedBody.requestBodyJson);
        }

        private void runBasicPostmanAssertions(List<String> testLines, Response response, JsonNode responseJson) {
            String responseText = response.asString();

            if (containsLine(testLines, "pm.expect(pm.response.text()).to.eql(\"\");")) {
                assertEquals("", responseText, "Response body should be empty string");
            }
            if (containsLine(testLines, "pm.expect(pm.response.text()).to.not.be.empty;")) {
                assertFalse(responseText.isEmpty(), "Response body should not be empty");
            }
            if (containsLine(testLines, "pm.expect(jsonData).to.be.empty;")) {
                assertNotNull(responseJson, "Expected JSON body to assert empty jsonData");
                assertTrue(
                        (responseJson.isArray() || responseJson.isObject()) && responseJson.isEmpty(),
                        "Expected jsonData to be empty"
                );
            }
            if (containsLine(testLines, "pm.expect(jsonData).to.have.lengthOf(1);")) {
                assertNotNull(responseJson, "Expected JSON body to assert jsonData length");
                if (responseJson.isArray()) {
                    assertEquals(1, responseJson.size(), "Expected jsonData array size = 1");
                } else if (responseJson.isObject()) {
                    assertEquals(1, responseJson.size(), "Expected jsonData object size = 1");
                } else {
                    fail("Expected array/object for length assertion");
                }
            }
        }

        private void extractAndPersistVariables(
                List<String> testLines,
                JsonNode responseJson,
                JsonNode requestBodyJson
        ) {
            for (String line : testLines) {
                Matcher setMatcher = ENV_SET_PATTERN.matcher(line.trim());
                if (!setMatcher.find()) {
                    continue;
                }
                String variableName = setMatcher.group(1);
                String expression = setMatcher.group(2).trim();
                String value = resolveSetExpression(expression, responseJson, requestBodyJson);
                if (value != null) {
                    variables.put(variableName, value);
                }
            }
        }

        private String resolveSetExpression(String expression, JsonNode responseJson, JsonNode requestBodyJson) {
            if (expression.startsWith("\"") && expression.endsWith("\"")) {
                return expression.substring(1, expression.length() - 1);
            }

            Matcher jsonDataMatcher = SIMPLE_JSON_DATA_SET_PATTERN.matcher(expression);
            if (jsonDataMatcher.find()) {
                JsonNode valueNode = resolvePath(responseJson, jsonDataMatcher.group(1));
                return valueNode == null || valueNode.isNull() ? null : valueNode.asText();
            }

            Matcher requestBodyMatcher = SIMPLE_REQUEST_BODY_SET_PATTERN.matcher(expression);
            if (requestBodyMatcher.find()) {
                JsonNode valueNode = resolvePath(requestBodyJson, requestBodyMatcher.group(1));
                return valueNode == null || valueNode.isNull() ? null : valueNode.asText();
            }

            return null;
        }

        private JsonNode resolvePath(JsonNode node, String path) {
            if (node == null || path == null || path.isBlank()) {
                return null;
            }
            JsonNode current = node;
            String normalized = path.replace("[", ".").replace("]", "");
            String[] parts = normalized.split("\\.");
            for (String part : parts) {
                if (part.isBlank()) {
                    continue;
                }
                if (current == null || current.isMissingNode() || current.isNull()) {
                    return null;
                }
                if (part.chars().allMatch(Character::isDigit)) {
                    current = current.path(Integer.parseInt(part));
                } else {
                    current = current.path(part);
                }
            }
            return current == null || current.isMissingNode() ? null : current;
        }

        private void runPreRequestScript(List<String> preRequestLines) {
            if (preRequestLines.isEmpty()) {
                return;
            }

            Map<String, String> locals = new HashMap<>();
            for (String originalLine : preRequestLines) {
                String line = originalLine.trim();
                if (line.equals("const uniquePart = String(Date.now()).slice(-10);")) {
                    String millis = String.valueOf(System.currentTimeMillis());
                    locals.put("uniquePart", millis.substring(Math.max(0, millis.length() - 10)));
                }

                Matcher matcher = ENV_SET_PATTERN.matcher(line);
                if (!matcher.find()) {
                    continue;
                }
                String key = matcher.group(1);
                String rawExpression = matcher.group(2).trim();
                String value = resolvePreRequestExpression(rawExpression, locals);
                if (value != null) {
                    variables.put(key, value);
                }
            }
        }

        private String resolvePreRequestExpression(String expression, Map<String, String> locals) {
            if (expression.startsWith("`") && expression.endsWith("`")) {
                return interpolateTemplateLiteral(expression.substring(1, expression.length() - 1), locals);
            }
            if (expression.startsWith("\"") && expression.endsWith("\"")) {
                return resolveTemplate(expression.substring(1, expression.length() - 1));
            }
            return null;
        }

        private String interpolateTemplateLiteral(String template, Map<String, String> locals) {
            String value = template;
            value = value.replace("${Date.now()}", String.valueOf(System.currentTimeMillis()));
            for (Map.Entry<String, String> local : locals.entrySet()) {
                value = value.replace("${" + local.getKey() + "}", local.getValue());
            }
            return resolveTemplate(value);
        }

        private JsonNode tryParseJson(Response response) {
            String text = response.asString();
            if (text == null || text.isBlank()) {
                return null;
            }
            try {
                return mapper.readTree(text);
            } catch (IOException e) {
                return null;
            }
        }

        private int extractExpectedStatus(PostmanRequest request) {
            for (String line : request.testLines) {
                Matcher matcher = STATUS_PATTERN.matcher(line.trim());
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
            Matcher fromNameMatcher = STATUS_FROM_NAME_PATTERN.matcher(request.fullName);
            if (fromNameMatcher.find()) {
                return Integer.parseInt(fromNameMatcher.group(1));
            }
            throw new IllegalStateException("Expected status not found for request: " + request.fullName);
        }

        private ResolvedBody resolveBody(
                String bodyMode,
                String rawBody,
                List<Map.Entry<String, String>> urlencodedItems
        ) {
            if ("raw".equals(bodyMode) && rawBody != null) {
                String resolved = resolveTemplate(rawBody);
                JsonNode requestBodyJson = null;
                try {
                    requestBodyJson = mapper.readTree(resolved);
                } catch (Exception ignored) {
                    // Not a JSON body, variable extraction from requestBody is skipped for this request.
                }
                return new ResolvedBody(resolved, Map.of(), requestBodyJson);
            }
            if ("urlencoded".equals(bodyMode)) {
                Map<String, String> form = new LinkedHashMap<>();
                for (Map.Entry<String, String> item : urlencodedItems) {
                    form.put(item.getKey(), resolveTemplate(item.getValue()));
                }
                return new ResolvedBody(null, form, null);
            }
            return new ResolvedBody(null, Map.of(), null);
        }

        private String resolveTemplate(String text) {
            if (text == null) {
                return null;
            }
            Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(text);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String variable = matcher.group(1);
                String value = variables.getOrDefault(variable, "");
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(result);
            return result.toString();
        }

        private void loadEnvironmentVariables(JsonNode environment) {
            for (JsonNode valueNode : environment.path("values")) {
                if (!valueNode.path("enabled").asBoolean(true)) {
                    continue;
                }
                variables.put(valueNode.path("key").asText(), valueNode.path("value").asText());
            }
        }

        private void loadCollectionVariables(JsonNode collection) {
            for (JsonNode valueNode : collection.path("variable")) {
                variables.put(valueNode.path("key").asText(), valueNode.path("value").asText());
            }
        }

        private void flattenRequests(JsonNode items, String parentPath, List<PostmanRequest> target) {
            for (JsonNode item : items) {
                String currentName = item.path("name").asText();
                String fullName = parentPath.isBlank() ? currentName : parentPath + "/" + currentName;

                JsonNode nested = item.path("item");
                if (nested.isArray() && nested.size() > 0) {
                    flattenRequests(nested, fullName, target);
                    continue;
                }

                JsonNode request = item.path("request");
                String method = request.path("method").asText();
                String rawUrl = request.path("url").path("raw").asText();

                List<PostmanHeader> headers = new ArrayList<>();
                for (JsonNode headerNode : request.path("header")) {
                    headers.add(new PostmanHeader(
                            headerNode.path("key").asText(null),
                            headerNode.path("value").asText(null),
                            !headerNode.path("disabled").asBoolean(false)
                    ));
                }

                JsonNode bodyNode = request.path("body");
                String mode = bodyNode.path("mode").asText(null);
                String rawBody = bodyNode.path("raw").asText(null);
                List<Map.Entry<String, String>> urlencodedItems = new ArrayList<>();
                for (JsonNode formItem : bodyNode.path("urlencoded")) {
                    if (formItem.path("disabled").asBoolean(false)) {
                        continue;
                    }
                    urlencodedItems.add(Map.entry(
                            formItem.path("key").asText(),
                            formItem.path("value").asText("")
                    ));
                }

                List<String> preRequestLines = new ArrayList<>();
                List<String> testLines = new ArrayList<>();
                for (JsonNode eventNode : item.path("event")) {
                    String listen = eventNode.path("listen").asText();
                    List<String> scriptLines = new ArrayList<>();
                    for (JsonNode lineNode : eventNode.path("script").path("exec")) {
                        scriptLines.add(lineNode.asText());
                    }
                    if (Objects.equals(listen, "prerequest")) {
                        preRequestLines.addAll(scriptLines);
                    } else if (Objects.equals(listen, "test")) {
                        testLines.addAll(scriptLines);
                    }
                }

                target.add(new PostmanRequest(
                        fullName,
                        method,
                        rawUrl,
                        headers,
                        mode,
                        rawBody,
                        urlencodedItems,
                        preRequestLines,
                        testLines
                ));
            }
        }

        private boolean containsLine(List<String> lines, String candidate) {
            for (String line : lines) {
                if (line.trim().equals(candidate)) {
                    return true;
                }
            }
            return false;
        }
    }

    static final class PostmanRequest {
        private final String fullName;
        private final String method;
        private final String rawUrl;
        private final List<PostmanHeader> headers;
        private final String bodyMode;
        private final String rawBody;
        private final List<Map.Entry<String, String>> urlencodedItems;
        private final List<String> preRequestLines;
        private final List<String> testLines;

        private PostmanRequest(
                String fullName,
                String method,
                String rawUrl,
                List<PostmanHeader> headers,
                String bodyMode,
                String rawBody,
                List<Map.Entry<String, String>> urlencodedItems,
                List<String> preRequestLines,
                List<String> testLines
        ) {
            this.fullName = fullName;
            this.method = method;
            this.rawUrl = rawUrl;
            this.headers = headers;
            this.bodyMode = bodyMode;
            this.rawBody = rawBody;
            this.urlencodedItems = urlencodedItems;
            this.preRequestLines = preRequestLines;
            this.testLines = testLines;
        }
    }

    static final class PostmanHeader {
        private final String key;
        private final String value;
        private final boolean enabled;

        private PostmanHeader(String key, String value, boolean enabled) {
            this.key = key;
            this.value = value;
            this.enabled = enabled;
        }
    }

    static final class ResolvedBody {
        private final String rawBody;
        private final Map<String, String> urlencodedBody;
        private final JsonNode requestBodyJson;

        private ResolvedBody(String rawBody, Map<String, String> urlencodedBody, JsonNode requestBodyJson) {
            this.rawBody = rawBody;
            this.urlencodedBody = urlencodedBody;
            this.requestBodyJson = requestBodyJson;
        }
    }
}
