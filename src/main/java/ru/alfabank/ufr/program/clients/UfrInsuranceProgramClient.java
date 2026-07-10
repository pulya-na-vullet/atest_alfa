package ru.alfabank.ufr.program.clients;

import io.restassured.response.Response;
import ru.alfabank.configs.Endpoints;
import ru.alfabank.ufr.program.specifications.UfrSpecifications;

import static io.restassured.RestAssured.given;

public class UfrInsuranceProgramClient {

    public Response getInsurancePrograms() {
        return given()
                .spec(UfrSpecifications.requestSpec())
                .when()
                .get(Endpoints.UFR_INSURANCE_PROGRAMS)
                .then()
                .extract()
                .response();
    }
}