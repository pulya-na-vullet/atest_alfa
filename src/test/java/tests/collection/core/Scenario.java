package tests.collection.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scenario {
    private final String id;
    private final String fullName;
    private final String section;
    private final String method;
    private final String rawUrl;
    private final Map<String, String> headers;
    private final String bodyMode;
    private final String rawBody;
    private final List<FormItem> urlencoded;
    private final List<String> preRequestLines;
    private final List<String> testLines;

    public Scenario(
            String id,
            String fullName,
            String section,
            String method,
            String rawUrl,
            Map<String, String> headers,
            String bodyMode,
            String rawBody,
            List<FormItem> urlencoded,
            List<String> preRequestLines,
            List<String> testLines
    ) {
        this.id = id;
        this.fullName = fullName;
        this.section = section;
        this.method = method;
        this.rawUrl = rawUrl;
        this.headers = headers;
        this.bodyMode = bodyMode;
        this.rawBody = rawBody;
        this.urlencoded = urlencoded;
        this.preRequestLines = preRequestLines;
        this.testLines = testLines;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSection() { return section; }
    public String getMethod() { return method; }
    public String getRawUrl() { return rawUrl; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBodyMode() { return bodyMode; }
    public String getRawBody() { return rawBody; }
    public List<FormItem> getUrlencoded() { return urlencoded; }
    public List<String> getPreRequestLines() { return preRequestLines; }
    public List<String> getTestLines() { return testLines; }

    public static Map<String, String> headers(Object... args) {
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            result.put((String) args[i], (String) args[i + 1]);
        }
        return result;
    }

    public record FormItem(String key, String value) {}
}
