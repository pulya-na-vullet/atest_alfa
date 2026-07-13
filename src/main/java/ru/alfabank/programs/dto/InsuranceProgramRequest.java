package ru.alfabank.programs.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InsuranceProgramRequest {
    private Long programId;
    private String programCode;
    private String programName;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer minDuration;
    private Integer maxDuration;
    private BigDecimal insurancePremium;
    private List<String> description;
}
