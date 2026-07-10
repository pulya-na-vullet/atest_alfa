package tests.collection.auth;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import tests.collection.core.BaseSectionTest;

@Epic("Strahovanie Uchet")
@Feature("Avtorizatsiya")
@Story("Postman 1:1 migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
public class AuthApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Poluchenie tokena - 200")
    void poluchenie_tokena_200() {
        run("case_01");
    }

    @Test
    @Order(2)
    @DisplayName("Poluchenie spravochnika strahovyh programm - 401 expired token")
    void poluchenie_spravochnika_strahovyh_programm_401_expired_token() {
        run("case_02");
    }

    @Test
    @Order(3)
    @DisplayName("Poluchenie tokena - 400 nevernyy client_id")
    void poluchenie_tokena_400_nevernyi_client_id() {
        run("case_03");
    }

    @Test
    @Order(4)
    @DisplayName("Poluchenie tokena - 400 nevernyy grant_type")
    void poluchenie_tokena_400_nevernyi_grant_type() {
        run("case_04");
    }

    @Test
    @Order(5)
    @DisplayName("Poluchenie strahovoy programmy - 403 without token")
    void poluchenie_strahovoi_programmy_403_without_token() {
        run("case_05");
    }

    @Test
    @Order(6)
    @DisplayName("Poluchenie strahovoy programmy - 401 invalid token")
    void poluchenie_strahovoi_programmy_401_invalid_token() {
        run("case_06");
    }

}