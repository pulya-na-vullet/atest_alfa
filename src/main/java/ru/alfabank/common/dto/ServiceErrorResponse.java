package ru.alfabank.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceErrorResponse {

    private String source;
    private String type;
    private String code;
    private String internalMessage;
    private List<Map<String, Object>> details;
}
