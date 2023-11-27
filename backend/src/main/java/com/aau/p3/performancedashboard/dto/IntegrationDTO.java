package com.aau.p3.performancedashboard.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class IntegrationDTO {
    
    @NotBlank(message = "The integration name is required.")
    @Size(min = 3, max = 25, message = "The name must be from 3 to 25 characters.")
    @Schema(name = "name", example = "sales", required = true)
    private String name;
    
    @NotBlank(message = "The integration name is required.")
    @Schema(name = "type", example = "internal", required = true)
    private String type;
    
    //@Getter
    // @NotBlank(message = "The integration name is required.")
    //@Schema(name = "fields", example = "fields", required = true)
    //private Map<String, String> fields;
}
