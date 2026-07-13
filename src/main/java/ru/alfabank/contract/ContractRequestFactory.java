package ru.alfabank.contract;

import ru.alfabank.contract.dto.ContractRequest;
import ru.alfabank.contract.dto.InsuranceObject;
import ru.alfabank.contract.dto.Owner;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class ContractRequestFactory {

    public static ContractRequest defaultRequest(int programId) {
        return ContractRequest.builder()
                .programId(Long.valueOf(programId))
                .programCode("ACCOUNT")
                .signDate(OffsetDateTime.now().toString())
                .beginDate(OffsetDateTime.now().minusHours(3).toString())
                .endDate(OffsetDateTime.now().plusYears(1).toString())
                .duration(12)
                .paymentType("payment_account")
                .contractNumber("Z6922/395/22719794/009")
                .insuranceSum(BigDecimal.valueOf(123000))
                .insurancePremium(BigDecimal.valueOf(10000))
                .sellerId("U_M2XXX")
                .sellerChannel("SFA")
                .contractLink("https://test.local/contract.pdf")
                .policyLink("https://test.local/policy.pdf")
                .agreementLink("https://test.local/agreement.pdf")
                .verificationStatus("NEW")
                .owner(defaultOwner())
                .insuranceObjects(List.of(defaultInsuranceObject()))
                .build();
    }

    private static Owner defaultOwner() {
        return Owner.builder()
                .ownerId("U_M2XXX")
                .inn("7701234567")
                .phoneNumber("79999999999")
                .email("test@example.ru")
                .legalAddress("г. Москва, ул. Тестовая, д. 1")
                .build();
    }

    private static InsuranceObject defaultInsuranceObject() {
        return InsuranceObject.builder()
                .paymentAccount("40702810900000000001")
                .build();
    }
}
