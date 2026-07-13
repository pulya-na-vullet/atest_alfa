package tests.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.contract.clients.ContractReadClient;
import tests.BaseApiTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye dogovory")
@Story("Scenarii polucheniya strahovyh dogovorov")
public class ContractReadApiTests extends BaseApiTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ContractReadClient contractReadClient = new ContractReadClient();

    @Test
    @DisplayName("Poluchenie spiska strahovyh dogovorov po ownerId - 200")
    @Description("Proverka polucheniya spiska dogovorov po ownerId.")
    void getContractsByOwnerId_returns200() throws Exception {
        String ownerId = System.getProperty("contracts.ownerId", "TEST_OWNER_001");

        Response response = contractReadClient.getContractsByOwnerId(token, ownerId);
        assertEquals(200, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.isArray());
    }

    @Test
    @DisplayName("Poluchenie strahovogo dogovora po contractNumber - 200")
    @Description("Proverka polucheniya dogovora po contractNumber.")
    void getContractByContractNumber_returns200() throws Exception {
        String contractNumber = System.getProperty("contracts.number", "Z6922/888/ABR00177/6");

        Response response = contractReadClient.getContractsByContractNumber(token, contractNumber);
        assertEquals(200, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.isArray());
    }
}
