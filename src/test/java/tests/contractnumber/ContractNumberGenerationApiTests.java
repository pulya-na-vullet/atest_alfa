package tests.contractnumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.contract.clients.ContractNumberClient;
import tests.BaseApiTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Strahovanie Uchet")
@Feature("API Generatsiya nomera dogovora")
@Story("Scenarii generatsii nomera dogovora")
public class ContractNumberGenerationApiTests extends BaseApiTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ContractNumberClient contractNumberClient = new ContractNumberClient();

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora ACCOUNT - 201")
    @Description("Proverka uspeshnoy generatsii nomera dogovora dlya ACCOUNT.")
    void generateContractNumberForAccount_returns201() throws Exception {
        Response response = contractNumberClient.generateContractNumber(token, 1);

        assertEquals(201, response.getStatusCode());
        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.hasNonNull("contractNumber"));
        assertFalse(json.path("contractNumber").asText().isBlank());
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 unsupported program")
    @Description("Proverka oshibki pri nepodderzhivaemom programme.")
    void generateContractNumberForUnsupportedProgram_returns400() throws Exception {
        Response response = contractNumberClient.generateContractNumber(token, 4);

        assertEquals(400, response.getStatusCode());
        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 program not found")
    @Description("Proverka oshibki pri nesuschestvuyuschem programId.")
    void generateContractNumberForNotExistingProgram_returns400() throws Exception {
        Response response = contractNumberClient.generateContractNumber(token, 999999);

        assertEquals(400, response.getStatusCode());
        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 inactive program")
    @Description("Proverka oshibki pri neaktivnoy programme.")
    void generateContractNumberForInactiveProgram_returns400() throws Exception {
        Response response = contractNumberClient.generateContractNumber(token, 13);

        assertEquals(400, response.getStatusCode());
        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }
}
