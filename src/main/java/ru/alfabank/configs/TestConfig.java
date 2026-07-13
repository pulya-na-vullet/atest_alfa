package ru.alfabank.configs;

import java.io.IOException;
import java.util.Properties;

public class TestConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(
                    TestConfig.class
                            .getClassLoader()
                            .getResourceAsStream("application.properties")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBaseUrl() {
        return PROPERTIES.getProperty("base.url");
    }

    public static String getClientId() {
        return PROPERTIES.getProperty("client.id");
    }

    public static String getClientSecret() {
        return PROPERTIES.getProperty("client.secret");
    }

}
