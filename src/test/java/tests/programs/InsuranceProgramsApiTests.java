package tests.programs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.programs.clients.InsuranceProgramsClient;
import tests.BaseApiTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye programmy")
@Story("Scenarii strahovyh programm")
public class InsuranceProgramsApiTests extends BaseApiTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final InsuranceProgramsClient client = new InsuranceProgramsClient();
    private final long dynamicProgramId = 900000L + System.currentTimeMillis() % 100000;

    @Test
    @DisplayName("Sozdanie strahovoy programmy - 201")
    @Description("Proverka uspeshnogo sozdaniya strahovoy programmy.")
    void createProgram_returns201() {
        Response response = client.createProgram(token, buildCreateProgramBody(dynamicProgramId));
        assertEquals(201, response.getStatusCode());
    }

    @Test
    @DisplayName("Sozdanie strahovoy programmy - 400 extra unsupported field")
    @Description("Proverka validatsii pri peredache lishnego polya.")
    void createProgram_withUnsupportedField_returns400() throws Exception {
        Map<String, Object> body = buildCreateProgramBody(dynamicProgramId + 1);
        body.put("unexpectedField", "unexpected value");
        Response response = client.createProgram(token, body);

        assertEquals(400, response.getStatusCode());
        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Poluchenie spravochnika strahovyh programm - 200")
    @Description("Proverka polucheniya nepustogo spiska programm.")
    void getPrograms_returns200AndNonEmptyArray() throws Exception {
        Response response = client.getPrograms(token);
        assertEquals(200, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.isArray());
        assertFalse(json.isEmpty());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 200")
    @Description("Proverka polucheniya programm po ID.")
    void getProgramById_returns200() throws Exception {
        Response response = client.getProgramById(token, "2");
        assertEquals(200, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertEquals(2L, json.path("programId").asLong());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 400 program not found")
    @Description("Proverka obrabotki nesuschestvuyuschego programId.")
    void getProgramById_withNotExistingId_returns400() throws Exception {
        Response response = client.getProgramById(token, "999999");
        assertEquals(400, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 400 invalid programId format")
    @Description("Proverka obrabotki nekorrektnogo formata programId.")
    void getProgramById_withInvalidFormat_returns400() throws Exception {
        Response response = client.getProgramById(token, "abc");
        assertEquals(400, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy EMPLOYEE_HEALTH - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy EMPLOYEE_HEALTH.")
    void updateEmployeeHealthProgram_returns200() {
        Response response = client.updateProgram(token, "3", buildEmployeeHealthUpdateBody());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy ACCOUNT - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy ACCOUNT.")
    void updateAccountProgram_returns200() {
        Response response = client.updateProgram(token, "1", buildAccountUpdateBody());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy PROPERTY - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy PROPERTY.")
    void updatePropertyProgram_returns200() {
        Response response = client.updateProgram(token, "2", buildPropertyUpdateBody());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy bez programCode.")
    void updateGenericProgram_returns200() {
        Response response = client.updateProgram(token, "2", buildGenericUpdateBody());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 400 program not found")
    @Description("Proverka oshibki pri obnovlenii nesuschestvuyuschey programmy.")
    void updateProgram_withNotExistingId_returns400() throws Exception {
        Response response = client.updateProgram(token, "999999", buildNotExistingUpdateBody());
        assertEquals(400, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 400 extra unsupported field")
    @Description("Proverka validatsii lishnego polya pri PUT.")
    void updateProgram_withUnsupportedField_returns400() throws Exception {
        Map<String, Object> body = buildGenericUpdateBody();
        body.put("unexpectedField", "unexpected value");

        Response response = client.updateProgram(token, "2", body);
        assertEquals(400, response.getStatusCode());

        JsonNode json = MAPPER.readTree(response.asString());
        assertTrue(json.has("code"));
    }

    private Map<String, Object> buildCreateProgramBody(long programId) {
        return Map.of(
                "programId", programId,
                "programCode", "TEST_PROGRAM_" + programId,
                "programName", "Testovaya programma strahovaniya",
                "minSum", 100000.00,
                "maxSum", 1000000.00,
                "minDuration", 12,
                "maxDuration", 24,
                "insurancePremium", 5000.00,
                "description", List.of(
                        "Testovoe opisanie 1",
                        "Testovoe opisanie 2",
                        "Testovoe opisanie 3"
                )
        );
    }

    private Map<String, Object> buildEmployeeHealthUpdateBody() {
        return Map.of(
                "programId", 3,
                "programCode", "EMPLOYEE_HEALTH",
                "programName", "DMS dlya biznesa - obnovlenie",
                "minSum", 15000.00,
                "maxSum", 1200000.00,
                "minDuration", 12,
                "maxDuration", 60,
                "insurancePremium", 5600.00,
                "description", List.of(
                        "Vyzov vracha na dom",
                        "Konsultatsii vrachey",
                        "Priem vrachey v klinikah i onlayn"
                )
        );
    }

    private Map<String, Object> buildAccountUpdateBody() {
        return Map.of(
                "programId", 1,
                "programCode", "ACCOUNT",
                "programName", "Zaschita scheta - obnovlenie",
                "minSum", 20000.00,
                "maxSum", 1250000.00,
                "minDuration", 12,
                "maxDuration", 36,
                "insurancePremium", 5200.00,
                "description", List.of(
                        "Kompensatsiya raskhodov pri blokirovke scheta",
                        "Pokrytie osnovnyh riskov",
                        "Obnovlenie dlya proverki PUT"
                )
        );
    }

    private Map<String, Object> buildPropertyUpdateBody() {
        return Map.of(
                "programId", 2,
                "programCode", "PROPERTY",
                "programName", "Zaschita imuschestva - obnovlenie",
                "minSum", 30000.00,
                "maxSum", 1500000.00,
                "minDuration", 12,
                "maxDuration", 39,
                "insurancePremium", 6100.00,
                "description", List.of(
                        "Strahovanie nedvizhimosti dlya korporativnyh klientov",
                        "Pokrytie osnovnyh imuschestvennyh riskov",
                        "Obnovlenie dlya proverki PUT"
                )
        );
    }

    private Map<String, Object> buildGenericUpdateBody() {
        return Map.of(
                "programId", 2,
                "programName", "Universalnaya testovaya programma - obnovlenie",
                "description", List.of(
                        "Universalnoe obnovlennoe opisanie programmy",
                        "Proverka PUT bez zhostkoy privyazki k programCode"
                ),
                "minSum", 10000.00,
                "maxSum", 1000000.00,
                "minDuration", 12,
                "maxDuration", 24
        );
    }

    private Map<String, Object> buildNotExistingUpdateBody() {
        return Map.of(
                "programId", 999999,
                "programName", "Obnovlennaya testovaya programma",
                "minSum", 15000.00,
                "maxSum", 1200000.00,
                "minDuration", 12,
                "maxDuration", 36,
                "insurancePremium", 5500.00,
                "description", List.of(
                        "Obnovlennoe opisanie programmy",
                        "Dopolnitelnoe opisanie programmy"
                )
        );
    }
}
