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
@Story("Scenarii sozdaniya strahovyh programm")
public class InsuranceProgramsCreateApiTests extends BaseApiTest {

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
}
