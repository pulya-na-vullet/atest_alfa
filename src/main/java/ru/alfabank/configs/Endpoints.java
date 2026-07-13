package ru.alfabank.configs;

public final class Endpoints {

    private Endpoints() {
    }

    public static final String TOKEN =
            "/mks-gateway/public/auth/realms/corporate/protocol/openid-connect/token";

    public static final String INSURANCE_PROGRAMS =
            "/corp-ncins-acc-gateway/secure/" +
                    "corp-ncins-acc-corp-ncins-acc-api/v1/ins-programs";

    public static final String CONTRACT_PROGRAMS =
            "/corp-ncins-acc-gateway/secure/" +
                    "corp-ncins-acc-corp-ncins-acc-api/v1/ins-contracts";

    public static final String UFR_INSURANCE_PROGRAMS =
            "/v1/ins-programs";
}
