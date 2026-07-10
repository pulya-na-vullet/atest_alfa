package ru.alfabank.auth.clients;

import io.restassured.http.ContentType;
import ru.alfabank.auth.dto.TokenResponse;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public String getToken() {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", TestConfig.getClientId())
                .formParam("client_secret", TestConfig.getClientSecret())
                .when()
                .post(Endpoints.TOKEN)
                .then()
                .statusCode(200)
                .extract()
                .as(TokenResponse.class)
                .getAccessToken();
    }
}
