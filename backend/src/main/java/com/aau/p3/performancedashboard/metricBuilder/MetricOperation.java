package com.aau.p3.performancedashboard.metricBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetricOperation {
    
    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Name of the operation", example = "Count berlingske salg", required = true)
    private String name;

    @NotBlank(message = "Target integration cannot be blank")
    @Schema(description = "Target integration of the operation", example = "sales-db", required = true)
    private String targetIntegration;

    @NotNull(message = "Type cannot be null")
    @Schema(description = "Type of the operation (SINGULAR or MULTIPLE)", example = "SINGULAR", required = true)
    private MetricOperationEnum type;

    @NotNull(message = "Operator cannot be null")
    @Schema(description = "Operator of the operation", example = "COUNT", required = true)
    private MetricOperationOperatorEnum operator;

    @Schema(description = "Criteria for the operation", example = "[{\"key\": \"field1\", \"value\": \"value1\"}]", required = false)
    @ArraySchema(schema = @Schema(implementation = Criteria.class))
    @Valid
    private List<Criteria> criteria;

    @Schema(description = "Type of multiple operand (if applicable)", example = "ADDITION", required = false)
    private String typeMultipleOperand;

    @Schema(description = "Type of multiple target (if applicable)", example = "PRODUCT", required = false)
    private String typeMultipleTarget;
}
