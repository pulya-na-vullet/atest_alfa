package ru.alfabank.contract.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.alfabank.configs.AccountingHeaders;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;

import static io.restassured.RestAssured.given;

public class ContractReadClient {

    public Response getContractsByOwnerId(String token, String ownerId) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .queryParam("ownerId", ownerId)
                .when()
                .get(Endpoints.CONTRACT_PROGRAMS)
                .then()
                .extract()
                .response();
    }

    public Response getContractsByContractNumber(String token, String contractNumber) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .queryParam("contractNumber", contractNumber)
                .when()
                .get(Endpoints.CONTRACT_PROGRAMS)
                .then()
                .extract()
                .response();
    }
}
