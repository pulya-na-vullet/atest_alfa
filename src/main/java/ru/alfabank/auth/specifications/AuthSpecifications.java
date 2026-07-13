package ru.alfabank.auth.specifications;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

public final class AuthSpecifications {

    private AuthSpecifications() {
    }

    public static ResponseSpecification responseSpec200Json() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpec400Json() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
