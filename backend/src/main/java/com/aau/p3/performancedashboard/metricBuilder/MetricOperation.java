package com.aau.p3.performancedashboard.metricBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetricOperation {
    
    @NotNull(message = "Operation must not be empty")
    @Schema(description = "Operation to be performed", example = "COUNT", required = true)
    private MetricOperationEnum operation;

    @Schema(description = "Name of the operation", example = "Count Berlingske Sales", required = false)
    private String name;

    @Schema(description = "Target integration of the operation", example = "657dc70fc773a63db12e5f97", required = false)
    private String targetIntegration;

    @Schema(description = "Operator for the operation", example = "ADD", required = false)
    private MetricOperationEnum operator;

    @Schema(description = "Criteria for the operation", example = "{\"data_Publikation\": \"Berlingske\", \"data_Type\": \"Aktivt salg\"}", required = false)    @ArraySchema(schema = @Schema(implementation = Criteria.class))
    @Valid
    private Map<String, String> criteria;
}
