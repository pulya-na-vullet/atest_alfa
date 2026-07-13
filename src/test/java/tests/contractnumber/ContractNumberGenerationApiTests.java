package tests.contractnumber;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.contract.ContractNumberRequestFactory;
import ru.alfabank.contract.clients.ContractNumberClient;
import ru.alfabank.contract.dto.ContractNumberRequest;
import ru.alfabank.contract.dto.ContractNumberResponse;
import ru.alfabank.contract.specifications.ContractSpecifications;
import ru.alfabank.common.dto.ServiceErrorResponse;
import tests.BaseApiTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Strahovanie Uchet")
@Feature("API Generatsiya nomera dogovora")
@Story("Scenarii generatsii nomera dogovora")
public class ContractNumberGenerationApiTests extends BaseApiTest {

    private final ContractNumberClient contractNumberClient = new ContractNumberClient();

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora ACCOUNT - 201")
    @Description("Proverka uspeshnoy generatsii nomera dogovora dlya ACCOUNT.")
    void generateContractNumberForAccount_returns201() {
        ContractNumberRequest request = ContractNumberRequestFactory.byProgramId(1);
        Response response = contractNumberClient.generateContractNumber(token, request);

        ContractNumberResponse contractNumberResponse = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec201Json())
                .extract()
                .as(ContractNumberResponse.class);
        assertNotNull(contractNumberResponse.getContractNumber());
        assertFalse(contractNumberResponse.getContractNumber().isBlank());
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 unsupported program")
    @Description("Proverka oshibki pri nepodderzhivaemom programme.")
    void generateContractNumberForUnsupportedProgram_returns400() {
        ContractNumberRequest request = ContractNumberRequestFactory.byProgramId(1);
        request.setProgramId(4L);
        Response response = contractNumberClient.generateContractNumber(token, request);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 program not found")
    @Description("Proverka oshibki pri nesuschestvuyuschem programId.")
    void generateContractNumberForNotExistingProgram_returns400() {
        ContractNumberRequest request = ContractNumberRequestFactory.byProgramId(999999);
        Response response = contractNumberClient.generateContractNumber(token, request);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }

    @Test
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 inactive program")
    @Description("Proverka oshibki pri neaktivnoy programme.")
    void generateContractNumberForInactiveProgram_returns400() {
        ContractNumberRequest request = ContractNumberRequestFactory.byProgramId(13);
        Response response = contractNumberClient.generateContractNumber(token, request);

        ServiceErrorResponse error = response.then()
                .log().ifValidationFails()
                .spec(ContractSpecifications.responseSpec400Json())
                .extract()
                .as(ServiceErrorResponse.class);
        assertNotNull(error.getCode());
    }
}
