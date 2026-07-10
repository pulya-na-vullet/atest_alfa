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
@Story("Postman 1:1 migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
public class ContractsApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Sozdanie strahovogo dogovora ACCOUNT - 201")
    void sozdanie_strahovogo_dogovora_account_201() {
        run("case_07");
    }

    @Test
    @Order(2)
    @DisplayName("Sozdanie strahovogo dogovora PROPERTY - 201")
    void sozdanie_strahovogo_dogovora_property_201() {
        run("case_08");
    }

    @Test
    @Order(3)
    @DisplayName("Sozdanie strahovogo dogovora EMPLOYEE_HEALTH - 201")
    void sozdanie_strahovogo_dogovora_employee_health_201() {
        run("case_09");
    }

    @Test
    @Order(4)
    @DisplayName("Sozdanie strahovogo dogovora EMPLOYEE_HEALTH - 400 irrelevant owner and insuranceObjects fields")
    void sozdanie_strahovogo_dogovora_employee_health_400_irrelevant_owner_and_insuranceobjects_fields() {
        run("case_10");
    }

    @Test
    @Order(5)
    @DisplayName("Sozdanie strahovogo dogovora - 400 program not found")
    void sozdanie_strahovogo_dogovora_400_program_not_found() {
        run("case_11");
    }

    @Test
    @Order(6)
    @DisplayName("Sozdanie strahovogo dogovora ACCOUNT - 400 irrelevant owner and insuranceObjects fields")
    void sozdanie_strahovogo_dogovora_account_400_irrelevant_owner_and_insuranceobjects_fields() {
        run("case_12");
    }

    @Test
    @Order(7)
    @DisplayName("Sozdanie strahovogo dogovora PROPERTY - 400 irrelevant insuranceObjects fields")
    void sozdanie_strahovogo_dogovora_property_400_irrelevant_insuranceobjects_fields() {
        run("case_13");
    }

    @Test
    @Order(8)
    @DisplayName("Sozdanie strahovogo dogovora ACCOUNT - 400 invalid contractNumber")
    void sozdanie_strahovogo_dogovora_account_400_invalid_contractnumber() {
        run("case_14");
    }

    @Test
    @Order(9)
    @DisplayName("Sozdanie strahovogo dogovora ACCOUNT - 400 reused USED contractNumber")
    void sozdanie_strahovogo_dogovora_account_400_reused_used_contractnumber() {
        run("case_15");
    }

    @Test
    @Order(10)
    @DisplayName("Poluchenie spiska strahovyh dogovorov po ownerId - 200")
    void poluchenie_spiska_strahovyh_dogovorov_po_ownerid_200() {
        run("case_16");
    }

    @Test
    @Order(11)
    @DisplayName("Poluchenie strahovogo dogovora po contractNumber - 200")
    void poluchenie_strahovogo_dogovora_po_contractnumber_200() {
        run("case_17");
    }

    @Test
    @Order(12)
    @DisplayName("Poluchenie strahovogo dogovora ACCOUNT - 200 only relevant fields")
    void poluchenie_strahovogo_dogovora_account_200_only_relevant_fields() {
        run("case_18");
    }

    @Test
    @Order(13)
    @DisplayName("Poluchenie strahovogo dogovora PROPERTY - 200 only relevant fields")
    void poluchenie_strahovogo_dogovora_property_200_only_relevant_fields() {
        run("case_19");
    }

    @Test
    @Order(14)
    @DisplayName("Poluchenie strahovogo dogovora EMPLOYEE_HEALTH - 200 only relevant fields")
    void poluchenie_strahovogo_dogovora_employee_health_200_only_relevant_fields() {
        run("case_20");
    }

    @Test
    @Order(15)
    @DisplayName("Poluchenie strahovogo dogovora - 200 empty result for non-existing contractNumber")
    void poluchenie_strahovogo_dogovora_200_empty_result_for_non_existing_contractnumber() {
        run("case_21");
    }

    @Test
    @Order(16)
    @DisplayName("Poluchenie strahovogo dogovora - 200 missing contractNumber")
    void poluchenie_strahovogo_dogovora_200_missing_contractnumber() {
        run("case_22");
    }

    @Test
    @Order(17)
    @DisplayName("Poluchenie strahovogo dogovora - 200 missing ownerId")
    void poluchenie_strahovogo_dogovora_200_missing_ownerid() {
        run("case_23");
    }

    @Test
    @Order(18)
    @DisplayName("Poluchenie strahovogo dogovora - 200 empty result for invalid contractNumber")
    void poluchenie_strahovogo_dogovora_200_empty_result_for_invalid_contractnumber() {
        run("case_24");
    }

    @Test
    @Order(19)
    @DisplayName("Poluchenie vseh strahovyh dogovorov - 200 empty result without filters")
    void poluchenie_vseh_strahovyh_dogovorov_200_empty_result_without_filters() {
        run("case_25");
    }

    @Test
    @Order(20)
    @DisplayName("Izmenenie strahovogo dogovora - 200 minimal body")
    void izmenenie_strahovogo_dogovora_200_minimal_body() {
        run("case_26");
    }

    @Test
    @Order(21)
    @DisplayName("Izmenenie strahovogo dogovora - 200 update verificationStatus")
    void izmenenie_strahovogo_dogovora_200_update_verificationstatus() {
        run("case_27");
    }

    @Test
    @Order(22)
    @DisplayName("Izmenenie strahovogo dogovora - 200 update debitAccount")
    void izmenenie_strahovogo_dogovora_200_update_debitaccount() {
        run("case_28");
    }

    @Test
    @Order(23)
    @DisplayName("Izmenenie strahovogo dogovora - 400 contractNumber in body")
    void izmenenie_strahovogo_dogovora_400_contractnumber_in_body() {
        run("case_29");
    }

    @Test
    @Order(24)
    @DisplayName("Izmenenie strahovogo dogovora - 400 invalid insuranceObjects for program")
    void izmenenie_strahovogo_dogovora_400_invalid_insuranceobjects_for_program() {
        run("case_30");
    }

    @Test
    @Order(25)
    @DisplayName("Izmenenie strahovogo dogovora - 400 invalid owner for program")
    void izmenenie_strahovogo_dogovora_400_invalid_owner_for_program() {
        run("case_31");
    }

    @Test
    @Order(26)
    @DisplayName("Izmenenie strahovogo dogovora - 400 null for non-nullable top-level field")
    void izmenenie_strahovogo_dogovora_400_null_for_non_nullable_top_level_field() {
        run("case_32");
    }

    @Test
    @Order(27)
    @DisplayName("Izmenenie strahovogo dogovora - 400 unsupported top-level field")
    void izmenenie_strahovogo_dogovora_400_unsupported_top_level_field() {
        run("case_33");
    }

}