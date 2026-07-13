package tests.programs;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.programs.clients.InsuranceProgramsClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Strahovanie Uchet")
@Feature("API Strahovye programmy")
@Story("Scenarii avtorizatsii endpointa strahovyh programm")
public class InsuranceProgramsAuthorizationApiTests {

    private final InsuranceProgramsClient client = new InsuranceProgramsClient();

    @Test
    @DisplayName("Poluchenie spravochnika strahovyh programm - 401 expired token")
    @Description("Proverka otkaza pri prosrochennom tokene.")
    void getPrograms_withExpiredToken_returns401() {
        Response response = client.getPrograms("expired_access_token");
        assertEquals(401, response.getStatusCode());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 403 without token")
    @Description("Proverka otkaza pri otsutstvii zagolovka Authorization.")
    void getProgram_withoutToken_returns403() {
        Response response = client.getProgramByIdWithoutToken("2");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 401 invalid token")
    @Description("Proverka otkaza pri nevernom znachenii Bearer token.")
    void getProgram_withInvalidToken_returns401() {
        Response response = client.getProgramById("invalid_token", "2");
        assertEquals(401, response.getStatusCode());
    }
}
