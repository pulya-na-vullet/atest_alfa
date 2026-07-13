package ru.alfabank.programs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceProgramResponse {
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
