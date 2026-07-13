package ru.alfabank.contract.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.alfabank.configs.AccountingHeaders;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ContractNumberClient {

    public Response generateContractNumber(String token, long programId) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("programId", programId))
                .when()
                .post(Endpoints.CONTRACT_PROGRAMS + "/contract-number")
                .then()
                .extract()
                .response();
    }
}
