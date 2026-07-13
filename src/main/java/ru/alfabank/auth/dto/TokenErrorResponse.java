package ru.alfabank.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenErrorResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;
}
