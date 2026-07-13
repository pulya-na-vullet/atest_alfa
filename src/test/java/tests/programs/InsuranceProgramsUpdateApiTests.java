package tests.programs;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.common.dto.ServiceErrorResponse;
import ru.alfabank.programs.InsuranceProgramRequestFactory;
import ru.alfabank.programs.clients.InsuranceProgramsClient;
import ru.alfabank.programs.dto.InsuranceProgramRequest;
import ru.alfabank.programs.specifications.ProgramSpecifications;
import tests.BaseApiTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye programmy")
@Story("Scenarii obnovleniya strahovyh programm")
public class InsuranceProgramsUpdateApiTests extends BaseApiTest {

    private final InsuranceProgramsClient client = new InsuranceProgramsClient();

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
