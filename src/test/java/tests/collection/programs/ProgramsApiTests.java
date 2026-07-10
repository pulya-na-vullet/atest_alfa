package tests.collection.programs;

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
@Feature("Strahovye programmy")
@Story("Postman 1:1 migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
public class ProgramsApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Sozdanie strahovoy programmy - 201")
    void sozdanie_strahovoi_programmy_201() {
        run("case_34");
    }

    @Test
    @Order(2)
    @DisplayName("Sozdanie strahovoy programmy - 400 extra unsupported field")
    void sozdanie_strahovoi_programmy_400_extra_unsupported_field() {
        run("case_35");
    }

    @Test
    @Order(3)
    @DisplayName("Poluchenie spravochnika strahovyh programm - 200")
    void poluchenie_spravochnika_strahovyh_programm_200() {
        run("case_36");
    }

    @Test
    @Order(4)
    @DisplayName("Poluchenie strahovoy programmy  - 200")
    void poluchenie_strahovoi_programmy_200() {
        run("case_37");
    }

    @Test
    @Order(5)
    @DisplayName("Poluchenie strahovoy programmy - 400 program not found")
    void poluchenie_strahovoi_programmy_400_program_not_found() {
        run("case_38");
    }

    @Test
    @Order(6)
    @DisplayName("Poluchenie strahovoy programmy - 400 invalid programId format")
    void poluchenie_strahovoi_programmy_400_invalid_programid_format() {
        run("case_39");
    }

    @Test
    @Order(7)
    @DisplayName("Izmenenie strahovoy programmy EMPLOYEE_HEALTH - 200")
    void izmenenie_strahovoi_programmy_employee_health_200() {
        run("case_40");
    }

    @Test
    @Order(8)
    @DisplayName("Izmenenie strahovoy programmy ACCOUNT - 200")
    void izmenenie_strahovoi_programmy_account_200() {
        run("case_41");
    }

    @Test
    @Order(9)
    @DisplayName("Izmenenie strahovoy programmy PROPERTY - 200")
    void izmenenie_strahovoi_programmy_property_200() {
        run("case_42");
    }

    @Test
    @Order(10)
    @DisplayName("Izmenenie strahovoy programmy - 200")
    void izmenenie_strahovoi_programmy_200() {
        run("case_43");
    }

    @Test
    @Order(11)
    @DisplayName("Izmenenie strahovoy programmy - 400 program not found")
    void izmenenie_strahovoi_programmy_400_program_not_found() {
        run("case_44");
    }

    @Test
    @Order(12)
    @DisplayName("Izmenenie strahovoy programmy - 400 extra unsupported field")
    void izmenenie_strahovoi_programmy_400_extra_unsupported_field() {
        run("case_45");
    }

}