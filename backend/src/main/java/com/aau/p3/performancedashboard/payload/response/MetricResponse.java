package com.aau.p3.performancedashboard.payload.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponse {

    private String id;
    
    @NotBlank(message = "Name must not be empty")
    @Schema(name = "name", example = "Berlingske salg", required = true)
    private String name;
    
    @NotBlank(message = "Dependent integration ids must not be empty")
    @Schema(name = "dependentIntegrationIds", example = "[\"655a40f451a6f25c2b736e30\"]", required = true)
    private List<String> dependentIntegrationIds;
}
