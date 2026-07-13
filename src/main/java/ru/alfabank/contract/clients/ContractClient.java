package ru.alfabank.contract.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.configs.TestConfig;
import ru.alfabank.contract.dto.ContractRequest;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ContractClient {

    public Response createContract(String token, ContractRequest request) {
        return given()
                .log().all()
                .baseUri(TestConfig.getBaseUrl())
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .headers(getAccountingHeaders())
                .body(request)
                .when()
                .post(Endpoints.CONTRACT_PROGRAMS)
                .then()
                .log().all()
                .extract()
                .response();
    }

    private Map<String, String> getAccountingHeaders() {
        return Map.of(
                "A-userId", "123456",
                "A-customerId", "123456",
                "A-clientType", "MOBILE",
                "A-channelId", "INTERNET"
        );
    }
}
