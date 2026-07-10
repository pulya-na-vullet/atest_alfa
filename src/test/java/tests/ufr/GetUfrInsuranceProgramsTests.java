package tests.ufr;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.ufr.program.clients.UfrInsuranceProgramClient;
import ru.alfabank.ufr.program.specifications.UfrSpecifications;
import io.restassured.common.mapper.TypeRef;
import ru.alfabank.ufr.program.dto.InsuranceProgramDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Страхование Учет")
@Feature("Прокси-методы UFR")
@Story("Получение списка страховых программ")
public class GetUfrInsuranceProgramsTests {

    private final UfrInsuranceProgramClient ufrInsuranceProgramClient = new UfrInsuranceProgramClient();

    @Test
    @DisplayName("Получение списка страховых программ UFR - 200")
    @Description("Проверка, что прокси-метод возвращает статус 200 и непустой список страховых программ.")
    public void getInsurancePrograms_returns200AndNonEmptyArray() {
        Response response = ufrInsuranceProgramClient.getInsurancePrograms();

        List<InsuranceProgramDto> programs = response.then()
                .log().ifValidationFails()
                .spec(UfrSpecifications.responseSpec200Json())
                .extract()
                .as(new TypeRef<>() {
                });

        assertNotNull(programs, "Список программ не должен быть null");
        assertFalse(programs.isEmpty(), "Список программ не должен быть пустым");
    }

    @Test
    @DisplayName("Все страховые программы UFR соответствуют контракту")
    @Description("Проверка, что каждая программа из ответа содержит обязательные поля.")
    public void getInsurancePrograms_returnsProgramsWithExpectedContract() {
        Response response = ufrInsuranceProgramClient.getInsurancePrograms();

        List<InsuranceProgramDto> programs = response.then()
                .log().ifValidationFails()
                .spec(UfrSpecifications.responseSpec200Json())
                .extract()
                .as(new TypeRef<>() {
                });

        assertNotNull(programs, "Список программ не должен быть null");
        assertFalse(programs.isEmpty(), "Список программ не должен быть пустым");

        programs.forEach(program -> {
            assertNotNull(program, "Программа не должна быть null");

            assertNotNull(program.getProgramId(), "У программы должен быть programId");
            assertNotNull(program.getProgramName(), "У программы должен быть programName");
            assertNotNull(program.getMinSum(), "У программы должен быть minSum");
            assertNotNull(program.getMaxSum(), "У программы должен быть maxSum");
            assertNotNull(program.getMinDuration(), "У программы должен быть minDuration");
            assertNotNull(program.getMaxDuration(), "У программы должен быть maxDuration");
            assertNotNull(program.getDescription(), "У программы должен быть description");

            for (String descriptionItem : program.getDescription()) {
                assertNotNull(descriptionItem, "Элемент description не должен быть null");
                assertFalse(descriptionItem.isBlank(), "Элемент description не должен быть пустым");
            }

            assertFalse(program.getProgramName().isBlank(), "programName не должен быть пустым");
            assertFalse(program.getDescription().isEmpty(), "description не должен быть пустым");

            if (program.getProgramCode() != null) {
                assertFalse(program.getProgramCode().isBlank(), "programCode не должен быть пустым");
            }
        });
    }
}