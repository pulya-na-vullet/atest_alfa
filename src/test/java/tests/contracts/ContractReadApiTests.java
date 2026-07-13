package tests.contracts;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.contract.clients.ContractReadClient;
import ru.alfabank.contract.dto.ContractResponse;
import ru.alfabank.contract.specifications.ContractSpecifications;
import tests.BaseApiTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye dogovory")
@Story("Scenarii polucheniya strahovyh dogovorov")
public class ContractReadApiTests extends BaseApiTest {

    private final ContractReadClient contractReadClient = new ContractReadClient();

    @Test
    @DisplayName("Poluchenie spiska strahovyh dogovorov po ownerId - 200")
    @Description("Proverka polucheniya spiska dogovorov po ownerId.")
    void getContractsByOwnerId_returns200() {
        String ownerId = System.getProperty("contracts.ownerId", "TEST_OWNER_001");

        Response response = contractReadClient.getContractsByOwnerId(token, ownerId);
        List<ContractResponse> contracts = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec200Json())
                .extract()
                .as(new TypeRef<>() {
                });

        assertNotNull(contracts);
    }

    @Test
    @DisplayName("Poluchenie strahovogo dogovora po contractNumber - 200")
    @Description("Proverka polucheniya dogovora po contractNumber.")
    void getContractByContractNumber_returns200() {
        String contractNumber = System.getProperty("contracts.number", "Z6922/888/ABR00177/6");

        Response response = contractReadClient.getContractsByContractNumber(token, contractNumber);
        List<ContractResponse> contracts = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec200Json())
                .extract()
                .as(new TypeRef<>() {
                });

        assertNotNull(contracts);
        if (!contracts.isEmpty()) {
            assertEquals(contractNumber, contracts.get(0).getContractNumber());
        }
    }
}
