package ru.alfabank.configs;

import java.util.Map;

public final class AccountingHeaders {

    private AccountingHeaders() {
    }

    public static Map<String, String> defaultHeaders() {
        return Map.of(
                "A-userId", "123456",
                "A-customerId", "123456",
                "A-clientType", "MOBILE",
                "A-channelId", "INTERNET"
        );
    }
}
