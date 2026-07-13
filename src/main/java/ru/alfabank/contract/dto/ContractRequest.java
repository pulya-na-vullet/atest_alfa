package ru.alfabank.contract.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ContractRequest {

    private Long programId;
    private String programCode;

    private String signDate;
    private String beginDate;
    private String endDate;

    private Integer duration;
    private String paymentType;
    private String contractNumber;

    private BigDecimal insuranceSum;
    private BigDecimal insurancePremium;

    private String sellerId;
    private String sellerChannel;

    private String contractLink;
    private String policyLink;
    private String agreementLink;

    private String verificationStatus;

    private Owner owner;

    private List<InsuranceObject> insuranceObjects;
}