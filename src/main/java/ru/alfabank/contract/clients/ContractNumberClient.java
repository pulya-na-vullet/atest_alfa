package ru.alfabank.contract.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.alfabank.contract.dto.ContractNumberRequest;
import ru.alfabank.configs.AccountingHeaders;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;

import static io.restassured.RestAssured.given;

public class ContractNumberClient {

    public Response generateContractNumber(String token, ContractNumberRequest request) {
        return given()
                .baseUri(TestConfig.getBaseUrl())
                .headers(AccountingHeaders.defaultHeaders())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post(Endpoints.CONTRACT_PROGRAMS + "/contract-number")
                .then()
                .extract()
                .response();
    }

    public Response generateContractNumber(String token, long programId) {
        ContractNumberRequest request = new ContractNumberRequest();
        request.setProgramId(programId);
        return generateContractNumber(token, request);
    }
}
