package ru.alfabank.contract.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractResponse {

    private String contractNumber;
    private Owner owner;
}
