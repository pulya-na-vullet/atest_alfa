package tests.collection.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.alfabank.configs.TestConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScenarioExecutor {

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");
    private static final Pattern STATUS_PATTERN = Pattern.compile("pm\\.response\\.to\\.have\\.status\\((\\d+)\\)");
    private static final Pattern STATUS_FROM_NAME_PATTERN = Pattern.compile("-\\s*(\\d{3})\\b");
    private static final Pattern SET_PATTERN = Pattern.compile("pm\\.(?:environment|variables)\\.set\\(\"([^\"]+)\",\\s*(.+)\\);");
    private static final Pattern EQL_STRING_PATTERN = Pattern.compile("pm\\.expect\\(([^)]+)\\)\\.to\\.eql\\(\"([^\"]*)\"\\);");
    private static final Pattern HAVE_PROPERTY_PATTERN = Pattern.compile(
            "pm\\.expect\\(([^)]+)\\)\\.to\\.have\\.property\\(\"([^\"]+)\"(?:,\\s*\"([^\"]*)\")?\\);"
    );
    private static final Pattern NOT_HAVE_PROPERTY_PATTERN = Pattern.compile(
            "pm\\.expect\\(([^)]+)\\)\\.to\\.not\\.have\\.property\\(\"([^\"]+)\"\\);"
    );

    private final Map<String, String> variables = new LinkedHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public ScenarioExecutor() {
        ScenarioData.initialVariables().forEach(variables::put);
        applyRuntimeOverrides();
        initializeDynamicVariables();
    }

    public void execute(Scenario scenario) {
        runPreRequest(scenario.getPreRequestLines());

        Response response = sendRequest(scenario, resolveTemplate(scenario.getRawUrl()));
        int expectedStatus = extractExpectedStatus(scenario);
        assertEquals(expectedStatus, response.statusCode(), "Unexpected status for: " + scenario.getFullName());

        JsonNode responseJson = parseJson(response.asString());
        JsonNode requestJson = parseJson(resolveTemplate(scenario.getRawBody()));

        runAssertions(scenario.getTestLines(), response, responseJson, requestJson, scenario);
        persistVariables(scenario.getTestLines(), responseJson, requestJson);
    }

    private Response sendRequest(Scenario scenario, String resolvedUrl) {
        var req = RestAssured.given().log().ifValidationFails();

        for (Map.Entry<String, String> h : scenario.getHeaders().entrySet()) {
            req.header(h.getKey(), resolveTemplate(h.getValue()));
        }

        if ("raw".equals(scenario.getBodyMode()) && scenario.getRawBody() != null) {
            if (!hasHeader(scenario, "content-type")) {
                req.header("Content-Type", "application/json");
            }
            if (!hasHeader(scenario, "accept")) {
                req.header("Accept", "application/json");
            }
            req.body(resolveTemplate(scenario.getRawBody()));
        }

        if ("urlencoded".equals(scenario.getBodyMode())) {
            for (Scenario.FormItem item : scenario.getUrlencoded()) {
                req.formParam(item.key(), resolveTemplate(item.value()));
            }
        }

        return req.request(scenario.getMethod(), resolvedUrl);
    }

    private boolean hasHeader(Scenario scenario, String headerName) {
        return scenario.getHeaders().keySet().stream()
                .anyMatch(key -> key != null && key.equalsIgnoreCase(headerName));
    }

    private void applyRuntimeOverrides() {
        String baseFromConfig = TestConfig.getBaseUrl();
        if (baseFromConfig != null && !baseFromConfig.isBlank()) {
            variables.put(
                    "baseUrl",
                    baseFromConfig + "/corp-ncins-acc-gateway/secure/corp-ncins-acc-corp-ncins-acc-api"
            );
            variables.put(
                    "tokenUrl",
                    baseFromConfig + "/mks-gateway/public/auth/realms/corporate/protocol/openid-connect/token"
            );
        }

        String baseOverride = System.getProperty("collection.baseUrl");
        if (baseOverride != null && !baseOverride.isBlank()) {
            variables.put("baseUrl", baseOverride);
        }
        String tokenOverride = System.getProperty("collection.tokenUrl");
        if (tokenOverride != null && !tokenOverride.isBlank()) {
            variables.put("tokenUrl", tokenOverride);
        }
    }

    private void initializeDynamicVariables() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String shortSuffix = suffix.substring(Math.max(0, suffix.length() - 8));

        setIfBlank("accountOwnerId", "TEST_OWNER_ACCOUNT_" + suffix);
        setIfBlank("propertyOwnerId", "TEST_OWNER_PROPERTY_" + suffix);
        setIfBlank("employeeHealthOwnerId", "TEST_OWNER_EH_" + suffix);

        setIfBlank("accountContractNumber", "Z6922/888/ABR" + shortSuffix + "/6");
        setIfBlank("propertyGeneratedContractNumber", "Z6922/888/ABR" + shortSuffix + "1/6");
        setIfBlank("employeeHealthGeneratedContractNumber", "Z6922/888/ABR" + shortSuffix + "2/6");
        setIfBlank("contractNumber", variables.get("accountContractNumber"));

        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        variables.put("invalidAccountContractNumber", "Z6922/888/ABR" + uuidPart + "/6");
    }

    private void setIfBlank(String key, String value) {
        String current = variables.get(key);
        if (current == null || current.isBlank()) {
            variables.put(key, value);
        }
    }

    private void runPreRequest(List<String> lines) {
        Map<String, String> locals = new LinkedHashMap<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.equals("const uniquePart = String(Date.now()).slice(-10);")) {
                String millis = String.valueOf(System.currentTimeMillis());
                locals.put("uniquePart", millis.substring(Math.max(0, millis.length() - 10)));
            }

            Matcher setMatcher = SET_PATTERN.matcher(trimmed);
            if (setMatcher.find()) {
                String key = setMatcher.group(1);
                String expr = setMatcher.group(2).trim();
                String value = evalPreRequestExpr(expr, locals);
                if (value != null) {
                    variables.put(key, value);
                }
            }
        }
    }

    private String evalPreRequestExpr(String expr, Map<String, String> locals) {
        if (expr.startsWith("`") && expr.endsWith("`")) {
            String t = expr.substring(1, expr.length() - 1);
            t = t.replace("${Date.now()}", String.valueOf(System.currentTimeMillis()));
            for (Map.Entry<String, String> e : locals.entrySet()) {
                t = t.replace("${" + e.getKey() + "}", e.getValue());
            }
            return resolveTemplate(t);
        }
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return resolveTemplate(expr.substring(1, expr.length() - 1));
        }
        return null;
    }

    private void runAssertions(
            List<String> lines,
            Response response,
            JsonNode responseJson,
            JsonNode requestJson,
            Scenario scenario
    ) {
        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.equals("pm.expect(pm.response.text()).to.eql(\"\");")
                    || trimmed.equals("pm.expect(responseText).to.eql(\"\");")) {
                assertEquals("", response.asString(), "Expected empty response text for: " + scenario.getFullName());
                continue;
            }

            if (trimmed.equals("pm.expect(pm.response.text()).to.not.be.empty;")
                    || trimmed.equals("pm.expect(responseText).to.not.be.empty;")) {
                assertFalse(response.asString().isEmpty(), "Expected non-empty response text for: " + scenario.getFullName());
                continue;
            }

            if (trimmed.equals("pm.expect(jsonData).to.be.empty;")) {
                assertNotNull(responseJson, "jsonData expected to be empty object/array");
                assertTrue((responseJson.isArray() || responseJson.isObject()) && responseJson.isEmpty(), "jsonData should be empty");
                continue;
            }

            if (trimmed.equals("pm.expect(jsonData).to.have.lengthOf(1);")) {
                assertNotNull(responseJson, "jsonData expected for length check");
                assertEquals(1, responseJson.size(), "jsonData length should be 1");
                continue;
            }

            if (trimmed.contains("to.be.an(\"array\").and.not.empty")) {
                JsonNode node = evalNodeRef(extractExpectReference(trimmed), responseJson, requestJson);
                assertNotNull(node, "Expected array node");
                assertTrue(node.isArray(), "Expected array type");
                assertTrue(node.size() > 0, "Expected non-empty array");
                continue;
            }

            if (trimmed.contains("to.be.an(\"object\").and.not.empty")) {
                JsonNode node = evalNodeRef(extractExpectReference(trimmed), responseJson, requestJson);
                assertNotNull(node, "Expected object node");
                assertTrue(node.isObject(), "Expected object type");
                assertTrue(node.size() > 0, "Expected non-empty object");
                continue;
            }

            if (trimmed.contains("token_type.toLowerCase()).to.eql(\"bearer\")") && responseJson != null) {
                assertEquals("bearer", responseJson.path("token_type").asText().toLowerCase());
                continue;
            }

            Matcher eqlMatcher = EQL_STRING_PATTERN.matcher(trimmed);
            if (eqlMatcher.find()) {
                JsonNode node = evalNodeRef(eqlMatcher.group(1), responseJson, requestJson);
                if (node != null && !node.isMissingNode()) {
                    assertEquals(eqlMatcher.group(2), node.asText(), "eql assertion mismatch");
                }
                continue;
            }

            Matcher propertyMatcher = HAVE_PROPERTY_PATTERN.matcher(trimmed);
            if (propertyMatcher.find()) {
                JsonNode node = evalNodeRef(propertyMatcher.group(1), responseJson, requestJson);
                if (node != null && node.isObject()) {
                    assertTrue(node.has(propertyMatcher.group(2)), "Expected property: " + propertyMatcher.group(2));
                    if (propertyMatcher.group(3) != null) {
                        assertEquals(propertyMatcher.group(3), node.path(propertyMatcher.group(2)).asText());
                    }
                }
                continue;
            }

            Matcher notPropertyMatcher = NOT_HAVE_PROPERTY_PATTERN.matcher(trimmed);
            if (notPropertyMatcher.find()) {
                JsonNode node = evalNodeRef(notPropertyMatcher.group(1), responseJson, requestJson);
                if (node != null && node.isObject()) {
                    assertFalse(node.has(notPropertyMatcher.group(2)), "Unexpected property: " + notPropertyMatcher.group(2));
                }
            }
        }
    }

    private String extractExpectReference(String line) {
        int from = line.indexOf('(');
        int to = line.indexOf(')');
        if (from >= 0 && to > from) {
            return line.substring(from + 1, to);
        }
        return "";
    }

    private void persistVariables(List<String> lines, JsonNode responseJson, JsonNode requestJson) {
        for (String line : lines) {
            Matcher matcher = SET_PATTERN.matcher(line.trim());
            if (!matcher.find()) {
                continue;
            }
            String key = matcher.group(1);
            String expr = matcher.group(2).trim();
            String value = resolveSetExpression(expr, responseJson, requestJson);
            if (value != null) {
                variables.put(key, value);
            }
        }
    }

    private String resolveSetExpression(String expr, JsonNode responseJson, JsonNode requestJson) {
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return expr.substring(1, expr.length() - 1);
        }
        if (expr.startsWith("jsonData.")) {
            JsonNode node = getPath(responseJson, expr.substring("jsonData.".length()));
            return node == null || node.isNull() ? null : node.asText();
        }
        if (expr.startsWith("requestBody.")) {
            JsonNode node = getPath(requestJson, expr.substring("requestBody.".length()));
            return node == null || node.isNull() ? null : node.asText();
        }
        return null;
    }

    private JsonNode evalNodeRef(String refExpr, JsonNode responseJson, JsonNode requestJson) {
        String ref = refExpr.trim();
        if (ref.startsWith("jsonData")) {
            String path = ref.equals("jsonData") ? "" : ref.substring("jsonData.".length());
            return path.isEmpty() ? responseJson : getPath(responseJson, path);
        }
        if (ref.startsWith("requestBody")) {
            String path = ref.equals("requestBody") ? "" : ref.substring("requestBody.".length());
            return path.isEmpty() ? requestJson : getPath(requestJson, path);
        }
        if (ref.startsWith("pm.response.text")) {
            return null;
        }
        return null;
    }

    private JsonNode getPath(JsonNode node, String path) {
        if (node == null) {
            return null;
        }
        if (path == null || path.isBlank()) {
            return node;
        }
        JsonNode current = node;
        String normalized = path.replace("[", ".").replace("]", "");
        for (String part : normalized.split("\\.")) {
            if (part.isBlank()) {
                continue;
            }
            if (current == null || current.isNull() || current.isMissingNode()) {
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

    private int extractExpectedStatus(Scenario scenario) {
        for (String line : scenario.getTestLines()) {
            Matcher m = STATUS_PATTERN.matcher(line.trim());
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        }
        Matcher fromName = STATUS_FROM_NAME_PATTERN.matcher(scenario.getFullName());
        if (fromName.find()) {
            return Integer.parseInt(fromName.group(1));
        }
        throw new IllegalStateException("Cannot infer expected status for " + scenario.getFullName());
    }

    private String resolveTemplate(String input) {
        if (input == null) {
            return null;
        }
        Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = variables.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private JsonNode parseJson(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return mapper.readTree(text);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static class ScenarioData {
        public static Map<String, String> initialVariables() {
            Map<String, String> vars = new LinkedHashMap<>();
            vars.put("baseUrl", "http://corp-gateway-test.moscow.alfaintra.net/corp-ncins-acc-gateway/secure/corp-ncins-acc-corp-ncins-acc-api");
            vars.put("accessToken", "");
            vars.put("programId", "2");
            vars.put("expectedTokenExpiresIn", "1800");
            vars.put("tokenUrl", "http://corp-gateway-test.moscow.alfaintra.net/mks-gateway/public/auth/realms/corporate/protocol/openid-connect/token");
            vars.put("expiredAccessToken", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkbUd0OXhCRnBlWnlveWxpRWgxVE1zdENGSFduTkJqd1hPaWF2aVZaRGJBIn0.eyJleHAiOjE3ODAzMDc5MzIsImlhdCI6MTc4MDMwNjEzMiwianRpIjoiYTkxN2E0MGUtMmYxNC00MzFjLWEyZjQtZjYwMjE3ZjZhYmJhIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLW5pYi1pbnQvcmVhbG1zL2NvcnBvcmF0ZSIsInN1YiI6ImQxYzBiZjM1LTExMGMtNGVlOC04ZTljLTA1N2YyMmU5MjE3NyIsInR5cCI6IkJlYXJlciIsImF6cCI6Im5pYi1jb3JwLW5jaW5zdXJhbmNlLWFjY291bnRpbmciLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIkFMTE9XX0NPUlBfTk9OX0NSRURJVF9JTlNVUkFOQ0VfQUNDT1VOVElOR19DT05UUkFDVFMiLCJBTExPV19DT1JQX05PTl9DUkVESVRfSU5TVVJBTkNFX0FDQ09VTlRJTkdfUFJPR1JBTVMiXX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SWQiOiJuaWItY29ycC1uY2luc3VyYW5jZS1hY2NvdW50aW5nIiwiY2xpZW50SG9zdCI6IjEwLjAuODguMTQ3IiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LW5pYi1jb3JwLW5jaW5zdXJhbmNlLWFjY291bnRpbmciLCJjbGllbnRBZGRyZXNzIjoiMTAuMC44OC4xNDcifQ.FEXOrSeXBq7yFcOjw8Pq6w90Tq6cx-hSrXxXRpXBYBmFoztbVbujUqGM6DalKVnWan3OFwl15xHIKGz1aBkiEnT_YoHaELb8b9Qp7Z--KGgQz9h3rcxrkl9wXyFpPdBwbk6qXrRIiu0nkVnGFcXZSVJ1sjTQ-E_PO_ZALLEYvKLWTN9Bj2_QD2Pgs2IY2ME5jQEjVW6uevVL0bnjALUsVZAOrYGJkVFIy2F_jK7uMUKit-yy58m2-n57NdEzb_bZdYW3rD4unpK7O4MmYbrBNd6AMpaM32g4aSiuWbtXB-I4o1t654ZulPWeiloUUyMWSpqw7QfdVwYRvmku_gyZrw");
            vars.put("createdOwnerId", "");
            vars.put("contractNumber", "Z6922/888/ABR00177/6");
            vars.put("accountContractNumber", "Z6922/888/ABR00177/6");
            vars.put("propertyContractNumber", "Z6922/888/ABR00022/6");
            vars.put("employeeHealthContractNumber", "Z6922/888/ABR00023/6");
            vars.put("debitAccountNumber", "43523463456734576458");
            vars.put("updatedDebitAccountNumber", "1234567890123456789445555");
            vars.put("unsupportedContractNumberGenerationProgramId", "4");
            vars.put("inactiveProgramId", "13");
            vars.put("invalidAccountContractNumber", "Z6922/888/ABR3275218751/6");
            vars.put("employeeHealthGeneratedContractNumber", "");
            vars.put("validGeneratedContractNumberForNegativePost", "");
            vars.put("propertyGeneratedContractNumber", "");
            vars.put("notExistingContractNumber", "Z9999/999/99999999/9");
            vars.put("invalidContractNumber", "invalid_contract");
            vars.put("aUserId", "123456");
            vars.put("aCustomerId", "123456");
            vars.put("aClientType", "MOBILE");
            vars.put("aChannelId", "INTERNET");
            vars.put("notExistingProgramId", "999999");
            vars.put("supportedProgramCodeAccount", "ACCOUNT");
            vars.put("supportedProgramCodeProperty", "PROPERTY");
            vars.put("supportedProgramCodeEmployeeHealth", "EMPLOYEE_HEALTH");
            vars.put("accountProgramId", "1");
            vars.put("propertyProgramId", "2");
            vars.put("employeeHealthProgramId", "3");
            vars.put("invalidProgramId", "abc");
            vars.put("invalidAccessToken", "invalid_token");
            vars.put("unexpectedFieldProgramId", "376672");
            vars.put("contractRulesByProgram", "{\n  \"ACCOUNT\": {\n    \"requiredTopLevel\": [\n      \"programId\",\n      \"contractNumber\",\n      \"signDate\",\n      \"beginDate\",\n      \"endDate\",\n      \"duration\",\n      \"paymentType\",\n      \"insuranceSum\",\n      \"insurancePremium\",\n      \"sellerChannel\",\n      \"contractLink\",\n      \"agreementLink\",\n      \"owner\",\n      \"insuranceObjects\"\n    ],\n    \"requiredOwnerFields\": [\"ownerId\", \"inn\", \"phoneNumber\", \"email\", \"legalAddress\"],\n    \"requiredInsuranceObjectFields\": [\"paymentAccount\"]\n  },\n  \"PROPERTY\": {\n    \"requiredTopLevel\": [\n      \"programId\",\n      \"contractNumber\",\n      \"signDate\",\n      \"beginDate\",\n      \"endDate\",\n      \"duration\",\n      \"paymentType\",\n      \"insuranceSum\",\n      \"insurancePremium\",\n      \"sellerChannel\",\n      \"contractLink\",\n      \"agreementLink\",\n      \"owner\",\n      \"insuranceObjects\"\n    ],\n    \"requiredOwnerFields\": [\"ownerId\", \"inn\", \"ogrn\", \"phoneNumber\", \"email\", \"legalAddress\"],\n    \"requiredInsuranceObjectFields\": [\"cadastralNumber\", \"area\", \"realEstateAddress\", \"realEstateType\"]\n  },\n  \"EMPLOYEE_HEALTH\": {\n    \"requiredTopLevel\": [\n      \"programId\",\n      \"contractNumber\",\n      \"signDate\",\n      \"beginDate\",\n      \"endDate\",\n      \"duration\",\n      \"paymentType\",\n      \"insurancePremium\",\n      \"sellerChannel\",\n      \"contractLink\",\n      \"agreementLink\",\n      \"owner\",\n      \"insuranceObjects\"\n    ],\n    \"requiredOwnerFields\": [\"ownerId\", \"inn\", \"phoneNumber\", \"email\"],\n    \"requiredInsuranceObjectFields\": [\"employeeFIO\", \"employeeEmail\", \"employeePhoneNumber\", \"employeeBirthDate\"]\n  }\n}");
            vars.put("optionalTopLevelFields", "[\"debitAccount\", \"sellerId\", \"policyLink\"]");
            vars.put("unsupportedProgramFields", "[]");
            vars.put("unsupportedProgramFieldsPut", "[]");
            return vars;
        }
    }
}
