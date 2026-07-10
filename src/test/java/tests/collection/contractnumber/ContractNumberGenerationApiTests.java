package tests.collection.contractnumber;

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
@Feature("Generatsiya nomera strahovogo dogovora")
@Story("Postman 1:1 migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
public class ContractNumberGenerationApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Generatsiya nomera strahovogo dogovora ACCOUNT - 201")
    void generaciya_nomera_strahovogo_dogovora_account_201() {
        run("case_46");
    }

    @Test
    @Order(2)
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 unsupported program")
    void generaciya_nomera_strahovogo_dogovora_400_unsupported_program() {
        run("case_47");
    }

    @Test
    @Order(3)
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 program not found")
    void generaciya_nomera_strahovogo_dogovora_400_program_not_found() {
        run("case_48");
    }

    @Test
    @Order(4)
    @DisplayName("Generatsiya nomera strahovogo dogovora - 400 inactive program")
    void generaciya_nomera_strahovogo_dogovora_400_inactive_program() {
        run("case_49");
    }

}