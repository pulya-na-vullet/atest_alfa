package tests.programs;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.common.dto.ServiceErrorResponse;
import ru.alfabank.programs.InsuranceProgramRequestFactory;
import ru.alfabank.programs.clients.InsuranceProgramsClient;
import ru.alfabank.programs.dto.InsuranceProgramRequest;
import ru.alfabank.programs.dto.InsuranceProgramResponse;
import ru.alfabank.programs.specifications.ProgramSpecifications;
import tests.BaseApiTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye programmy")
@Story("Scenarii strahovyh programm")
public class InsuranceProgramsApiTests extends BaseApiTest {

    private final InsuranceProgramsClient client = new InsuranceProgramsClient();
    private final long dynamicProgramId = 900000L + System.currentTimeMillis() % 100000;

    @Test
    @DisplayName("Sozdanie strahovoy programmy - 201")
    @Description("Proverka uspeshnogo sozdaniya strahovoy programmy.")
    void createProgram_returns201() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.createProgram(dynamicProgramId);
        Response response = client.createProgram(token, request);

        response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec201Json());
    }

    @Test
    @DisplayName("Sozdanie strahovoy programmy - 400 extra unsupported field")
    @Description("Proverka validatsii pri peredache lishnego polya.")
    void createProgram_withUnsupportedField_returns400() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.createProgram(dynamicProgramId + 1);
        Map<String, Object> body = new HashMap<>();
        body.put("programId", request.getProgramId());
        body.put("programCode", request.getProgramCode());
        body.put("programName", request.getProgramName());
        body.put("minSum", request.getMinSum());
        body.put("maxSum", request.getMaxSum());
        body.put("minDuration", request.getMinDuration());
        body.put("maxDuration", request.getMaxDuration());
        body.put("insurancePremium", request.getInsurancePremium());
        body.put("description", request.getDescription());
        body.put("unexpectedField", "unexpected value");
        Response response = client.createProgram(token, body);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Poluchenie spravochnika strahovyh programm - 200")
    @Description("Proverka polucheniya nepustogo spiska programm.")
    void getPrograms_returns200AndNonEmptyArray() {
        Response response = client.getPrograms(token);

        List<InsuranceProgramResponse> programs = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json())
                .extract()
                .as(new TypeRef<>() {
                });

        assertNotNull(programs);
        assertFalse(programs.isEmpty());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 200")
    @Description("Proverka polucheniya programm po ID.")
    void getProgramById_returns200() {
        Response response = client.getProgramById(token, "2");

        InsuranceProgramResponse program = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json())
                .extract()
                .as(InsuranceProgramResponse.class);

        assertEquals(2L, program.getProgramId());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 400 program not found")
    @Description("Proverka obrabotki nesuschestvuyuschego programId.")
    void getProgramById_withNotExistingId_returns400() {
        Response response = client.getProgramById(token, "999999");

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 400 invalid programId format")
    @Description("Proverka obrabotki nekorrektnogo formata programId.")
    void getProgramById_withInvalidFormat_returns400() {
        Response response = client.getProgramById(token, "abc");

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy EMPLOYEE_HEALTH - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy EMPLOYEE_HEALTH.")
    void updateEmployeeHealthProgram_returns200() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateEmployeeHealth();
        request.setProgramName("DMS dlya biznesa - obnovlenie");

        Response response = client.updateProgram(token, "3", request);
        response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy ACCOUNT - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy ACCOUNT.")
    void updateAccountProgram_returns200() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateAccount();
        request.setMinDuration(12);

        Response response = client.updateProgram(token, "1", request);
        response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy PROPERTY - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy PROPERTY.")
    void updatePropertyProgram_returns200() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateProperty();
        request.setMaxDuration(39);

        Response response = client.updateProgram(token, "2", request);
        response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 200")
    @Description("Proverka uspeshnogo obnovleniya programmy bez programCode.")
    void updateGenericProgram_returns200() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateGeneric(2);
        request.setProgramName("Universalnaya testovaya programma - obnovlenie");

        Response response = client.updateProgram(token, "2", request);
        response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec200Json());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 400 program not found")
    @Description("Proverka oshibki pri obnovlenii nesuschestvuyuschey programmy.")
    void updateProgram_withNotExistingId_returns400() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateNotExisting(999999);
        Response response = client.updateProgram(token, "999999", request);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Izmenenie strahovoy programmy - 400 extra unsupported field")
    @Description("Proverka validatsii lishnego polya pri PUT.")
    void updateProgram_withUnsupportedField_returns400() {
        InsuranceProgramRequest request = InsuranceProgramRequestFactory.updateGeneric(2);
        Map<String, Object> body = new HashMap<>();
        body.put("programId", request.getProgramId());
        body.put("programName", request.getProgramName());
        body.put("description", request.getDescription());
        body.put("minSum", request.getMinSum());
        body.put("maxSum", request.getMaxSum());
        body.put("minDuration", request.getMinDuration());
        body.put("maxDuration", request.getMaxDuration());
        body.put("unexpectedField", "unexpected value");

        Response response = client.updateProgram(token, "2", body);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ProgramSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }
}
