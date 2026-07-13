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
import ru.alfabank.programs.clients.InsuranceProgramsClient;
import ru.alfabank.programs.dto.InsuranceProgramResponse;
import ru.alfabank.programs.specifications.ProgramSpecifications;
import tests.BaseApiTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye programmy")
@Story("Scenarii polucheniya strahovyh programm")
public class InsuranceProgramsReadApiTests extends BaseApiTest {

    private final InsuranceProgramsClient client = new InsuranceProgramsClient();

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
}
