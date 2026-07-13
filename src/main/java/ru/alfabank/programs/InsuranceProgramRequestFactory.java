package ru.alfabank.programs;

import ru.alfabank.programs.dto.InsuranceProgramRequest;

import java.math.BigDecimal;
import java.util.List;

public final class InsuranceProgramRequestFactory {

    private InsuranceProgramRequestFactory() {
    }

    public static InsuranceProgramRequest createProgram(long programId) {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(programId);
        request.setProgramCode("TEST_PROGRAM_" + programId);
        request.setProgramName("Testovaya programma strahovaniya");
        request.setMinSum(BigDecimal.valueOf(100000));
        request.setMaxSum(BigDecimal.valueOf(1000000));
        request.setMinDuration(12);
        request.setMaxDuration(24);
        request.setInsurancePremium(BigDecimal.valueOf(5000));
        request.setDescription(List.of(
                "Testovoe opisanie 1",
                "Testovoe opisanie 2",
                "Testovoe opisanie 3"
        ));
        return request;
    }

    public static InsuranceProgramRequest updateEmployeeHealth() {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(3L);
        request.setProgramCode("EMPLOYEE_HEALTH");
        request.setProgramName("DMS dlya biznesa - obnovlenie");
        request.setMinSum(BigDecimal.valueOf(15000));
        request.setMaxSum(BigDecimal.valueOf(1200000));
        request.setMinDuration(12);
        request.setMaxDuration(60);
        request.setInsurancePremium(BigDecimal.valueOf(5600));
        request.setDescription(List.of(
                "Vyzov vracha na dom",
                "Konsultatsii vrachey",
                "Priem vrachey v klinikah i onlayn"
        ));
        return request;
    }

    public static InsuranceProgramRequest updateAccount() {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(1L);
        request.setProgramCode("ACCOUNT");
        request.setProgramName("Zaschita scheta - obnovlenie");
        request.setMinSum(BigDecimal.valueOf(20000));
        request.setMaxSum(BigDecimal.valueOf(1250000));
        request.setMinDuration(12);
        request.setMaxDuration(36);
        request.setInsurancePremium(BigDecimal.valueOf(5200));
        request.setDescription(List.of(
                "Kompensatsiya raskhodov pri blokirovke scheta",
                "Pokrytie osnovnyh riskov",
                "Obnovlenie dlya proverki PUT"
        ));
        return request;
    }

    public static InsuranceProgramRequest updateProperty() {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(2L);
        request.setProgramCode("PROPERTY");
        request.setProgramName("Zaschita imuschestva - obnovlenie");
        request.setMinSum(BigDecimal.valueOf(30000));
        request.setMaxSum(BigDecimal.valueOf(1500000));
        request.setMinDuration(12);
        request.setMaxDuration(39);
        request.setInsurancePremium(BigDecimal.valueOf(6100));
        request.setDescription(List.of(
                "Strahovanie nedvizhimosti dlya korporativnyh klientov",
                "Pokrytie osnovnyh imuschestvennyh riskov",
                "Obnovlenie dlya proverki PUT"
        ));
        return request;
    }

    public static InsuranceProgramRequest updateGeneric(long programId) {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(programId);
        request.setProgramName("Universalnaya testovaya programma - obnovlenie");
        request.setDescription(List.of(
                "Universalnoe obnovlennoe opisanie programmy",
                "Proverka PUT bez zhostkoy privyazki k programCode"
        ));
        request.setMinSum(BigDecimal.valueOf(10000));
        request.setMaxSum(BigDecimal.valueOf(1000000));
        request.setMinDuration(12);
        request.setMaxDuration(24);
        return request;
    }

    public static InsuranceProgramRequest updateNotExisting(long programId) {
        InsuranceProgramRequest request = new InsuranceProgramRequest();
        request.setProgramId(programId);
        request.setProgramName("Obnovlennaya testovaya programma");
        request.setMinSum(BigDecimal.valueOf(15000));
        request.setMaxSum(BigDecimal.valueOf(1200000));
        request.setMinDuration(12);
        request.setMaxDuration(36);
        request.setInsurancePremium(BigDecimal.valueOf(5500));
        request.setDescription(List.of(
                "Obnovlennoe opisanie programmy",
                "Dopolnitelnoe opisanie programmy"
        ));
        return request;
    }
}
