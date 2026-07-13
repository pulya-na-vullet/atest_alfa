package ru.alfabank.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class UfrTestConfig {

    private UfrTestConfig() {
    }

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = UfrTestConfig.class
                .getClassLoader()
                .getResourceAsStream("application-ufr-int.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("Файл application-ufr-int.properties не найден");
            }

            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRelaxedHttpsValidationEnabled() {
        return Boolean.parseBoolean(
                PROPERTIES.getProperty("ufr.relaxed-https-validation", "false")
        );
    }

    private static String getRequiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Отсутствует обязательное свойство: " + key);
        }

        return value;
    }

    public static String getBaseUrl() {
        return getRequiredProperty("ufr.base.url");
    }

    public static String getUserId() {
        return getRequiredProperty("ufr.a-user-id");
    }

    public static String getCustomerId() {
        return getRequiredProperty("ufr.a-customer-id");
    }

    public static String getClientType() {
        return getRequiredProperty("ufr.a-client-type");
    }

    public static String getChannelId() {
        return getRequiredProperty("ufr.a-channel-id");
    }
}