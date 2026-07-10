package tests.collection.contracts;

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
@Feature("Strahovye dogovory")
@Story("Postman scoped migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
public class ContractsApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Poluchenie spiska strahovyh dogovorov po ownerId - 200")
    void poluchenie_spiska_strahovyh_dogovorov_po_ownerid_200() {
        run("case_07");
    }

    @Test
    @Order(2)
    @DisplayName("Poluchenie strahovogo dogovora po contractNumber - 200")
    void poluchenie_strahovogo_dogovora_po_contractnumber_200() {
        run("case_08");
    }

}