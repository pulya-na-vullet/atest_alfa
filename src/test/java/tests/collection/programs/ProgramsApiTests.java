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

@Epic("Страхование Учет")
@Feature("Страховые программы")
@Story("Postman 1:1 migration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
public class ProgramsApiTests extends BaseSectionTest {

    @Test
    @Order(1)
    @DisplayName("Создание страховой программы - 201")
    void sozdanie_strahovoi_programmy_201() {
        run("case_34");
    }

    @Test
    @Order(2)
    @DisplayName("Создание страховой программы - 400 extra unsupported field")
    void sozdanie_strahovoi_programmy_400_extra_unsupported_field() {
        run("case_35");
    }

    @Test
    @Order(3)
    @DisplayName("Получение справочника страховых программ - 200")
    void poluchenie_spravochnika_strahovyh_programm_200() {
        run("case_36");
    }

    @Test
    @Order(4)
    @DisplayName("Получение страховой программы  - 200")
    void poluchenie_strahovoi_programmy_200() {
        run("case_37");
    }

    @Test
    @Order(5)
    @DisplayName("Получение страховой программы - 400 program not found")
    void poluchenie_strahovoi_programmy_400_program_not_found() {
        run("case_38");
    }

    @Test
    @Order(6)
    @DisplayName("Получение страховой программы - 400 invalid programId format")
    void poluchenie_strahovoi_programmy_400_invalid_programid_format() {
        run("case_39");
    }

    @Test
    @Order(7)
    @DisplayName("Изменение страховой программы EMPLOYEE_HEALTH - 200")
    void izmenenie_strahovoi_programmy_employee_health_200() {
        run("case_40");
    }

    @Test
    @Order(8)
    @DisplayName("Изменение страховой программы ACCOUNT - 200")
    void izmenenie_strahovoi_programmy_account_200() {
        run("case_41");
    }

    @Test
    @Order(9)
    @DisplayName("Изменение страховой программы PROPERTY - 200")
    void izmenenie_strahovoi_programmy_property_200() {
        run("case_42");
    }

    @Test
    @Order(10)
    @DisplayName("Изменение страховой программы - 200")
    void izmenenie_strahovoi_programmy_200() {
        run("case_43");
    }

    @Test
    @Order(11)
    @DisplayName("Изменение страховой программы - 400 program not found")
    void izmenenie_strahovoi_programmy_400_program_not_found() {
        run("case_44");
    }

    @Test
    @Order(12)
    @DisplayName("Изменение страховой программы - 400 extra unsupported field")
    void izmenenie_strahovoi_programmy_400_extra_unsupported_field() {
        run("case_45");
    }

}