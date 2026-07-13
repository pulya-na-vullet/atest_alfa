package tests.auth;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.auth.clients.AuthClient;
import ru.alfabank.auth.dto.TokenErrorResponse;
import ru.alfabank.auth.specifications.AuthSpecifications;
import ru.alfabank.configs.TestConfig;
import ru.alfabank.programs.clients.InsuranceProgramsClient;
import tests.BaseApiTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Epic("Strahovanie Uchet")
@Feature("API Avtorizatsiya")
@Story("Scenarii avtorizatsii")
public class AuthApiTests extends BaseApiTest {

    private final AuthClient authClient = new AuthClient();
    private final InsuranceProgramsClient insuranceProgramsClient = new InsuranceProgramsClient();

    @Test
    @DisplayName("Poluchenie tokena - 200")
    @Description("Proverka uspeshnogo polucheniya access token.")
    void getToken_returns200AndTokenPayload() {
        String accessToken = authClient.getToken();
        assertFalse(accessToken.isBlank());
    }

    @Test
    @DisplayName("Poluchenie spravochnika strahovyh programm - 401 expired token")
    @Description("Proverka otkaza pri prosrochennom tokene.")
    void getPrograms_withExpiredToken_returns401() {
        Response response = insuranceProgramsClient.getPrograms("expired_access_token");
        assertEquals(401, response.getStatusCode());
    }

    @Test
    @DisplayName("Poluchenie tokena - 400 nevernyy client_id")
    @Description("Proverka oshibki avtorizatsii pri nevernom client_id.")
    void getToken_withInvalidClientId_returns400() {
        Response response = authClient.requestToken(
                "client_credentials",
                "invalid_client",
                TestConfig.getClientSecret()
        );

        TokenErrorResponse body = response.then()
                .log().ifValidationFails()
                .spec(AuthSpecifications.responseSpec400Json())
                .extract()
                .as(TokenErrorResponse.class);
        assertEquals("invalid_client", body.getError());
    }

    @Test
    @DisplayName("Poluchenie tokena - 400 nevernyy grant_type")
    @Description("Proverka oshibki avtorizatsii pri nevernom grant_type.")
    void getToken_withInvalidGrantType_returns400() {
        Response response = authClient.requestToken(
                "invalid_grant",
                TestConfig.getClientId(),
                TestConfig.getClientSecret()
        );

        TokenErrorResponse body = response.then()
                .log().ifValidationFails()
                .spec(AuthSpecifications.responseSpec400Json())
                .extract()
                .as(TokenErrorResponse.class);
        assertEquals("unsupported_grant_type", body.getError());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 403 without token")
    @Description("Proverka otkaza pri otsutstvii zagolovka Authorization.")
    void getProgram_withoutToken_returns403() {
        Response response = insuranceProgramsClient.getProgramByIdWithoutToken("2");
        assertEquals(403, response.getStatusCode());
    }

    @Test
    @DisplayName("Poluchenie strahovoy programmy - 401 invalid token")
    @Description("Proverka otkaza pri nevernom znachenii Bearer token.")
    void getProgram_withInvalidToken_returns401() {
        Response response = insuranceProgramsClient.getProgramById("invalid_token", "2");
        assertEquals(401, response.getStatusCode());
    }
}
