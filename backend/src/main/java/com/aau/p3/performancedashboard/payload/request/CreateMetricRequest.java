package com.aau.p3.performancedashboard.payload.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateMetricRequest {
    
    @NotBlank(message = "Name must not be empty")
    @Schema(name = "name", example = "Berlingske salg", required = true)
    private String name;

    @NotEmpty(message = "Dependent integration ids must not be empty")
    @Schema(name = "dependentIntegrationIds", example = "[\"657dc70fc773a63db12e5f97\"]", required = true)
    private List<String> dependentIntegrationIds;
}
