package ru.alfabank.ufr.program.specifications;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import ru.alfabank.configs.UfrTestConfig;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;

public final class UfrSpecifications {

    private UfrSpecifications() {
    }

    public static RequestSpecification requestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(UfrTestConfig.getBaseUrl())
                .addHeader("Accept", ContentType.JSON.toString())
                .addHeaders(getUfrHeaders());

        if (UfrTestConfig.isRelaxedHttpsValidationEnabled()) {
            builder.setRelaxedHTTPSValidation();
        }

        return builder.build();
    }

    public static ResponseSpecification responseSpec200Json() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectHeader("Content-Type", containsString("application/json"))
                .build();
    }

    private static Map<String, String> getUfrHeaders() {
        return Map.of(
                "A-userId", UfrTestConfig.getUserId(),
                "A-customerId", UfrTestConfig.getCustomerId(),
                "A-clientType", UfrTestConfig.getClientType(),
                "A-channelId", UfrTestConfig.getChannelId()
        );
    }
}