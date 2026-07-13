package ru.alfabank.programs.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.alfabank.configs.AccountingHeaders;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;
import ru.alfabank.programs.dto.InsuranceProgramRequest;

import static io.restassured.RestAssured.given;

public class InsuranceProgramsClient {

    public Response createProgram(String token, InsuranceProgramRequest body) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post(Endpoints.INSURANCE_PROGRAMS)
                .then()
                .extract()
                .response();
    }

    public Response createProgram(String token, Object body) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post(Endpoints.INSURANCE_PROGRAMS)
                .then()
                .extract()
                .response();
    }

    public Response getPrograms(String token) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .when()
                .get(Endpoints.INSURANCE_PROGRAMS)
                .then()
                .extract()
                .response();
    }

    public Response getProgramById(String token, String programId) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .when()
                .get(Endpoints.INSURANCE_PROGRAMS + "/" + programId)
                .then()
                .extract()
                .response();
    }

    public Response getProgramByIdWithoutToken(String programId) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .accept(ContentType.JSON)
                .when()
                .get(Endpoints.INSURANCE_PROGRAMS + "/" + programId)
                .then()
                .extract()
                .response();
    }

    public Response updateProgram(String token, String programId, InsuranceProgramRequest body) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .put(Endpoints.INSURANCE_PROGRAMS + "/" + programId)
                .then()
                .extract()
                .response();
    }

    public Response updateProgram(String token, String programId, Object body) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .put(Endpoints.INSURANCE_PROGRAMS + "/" + programId)
                .then()
                .extract()
                .response();
    }
}
