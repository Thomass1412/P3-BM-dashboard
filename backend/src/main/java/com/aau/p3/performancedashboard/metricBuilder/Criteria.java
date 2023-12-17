package com.aau.p3.performancedashboard.metricBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {

    @NotBlank(message = "Key cannot be blank")
    @Schema(description = "Key for the criteria", example = "field1", required = true)
    private String key;

    @NotBlank(message = "Value cannot be blank")
    @Schema(description = "Value for the criteria", example = "value1", required = true)
    private String value;
}