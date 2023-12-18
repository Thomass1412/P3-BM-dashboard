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
@Schema(
    name = "MetricOperation",
    description = "An operation to be performed on a metric"
)
public class MetricOperation {
    
    @NotNull(message = "Operation must not be empty")
    @Schema(
        description = "The type of operation to be performed",
        example = "COUNT",
        required = true
    )
    private MetricOperationEnum operation;

    @Schema(
        description = "The name of the operation, for identification purposes",
        example = "Count Berlingske Sales",
        required = false
    )
    private String name;

    @Schema(
        description = "The target integration on which the operation is to be performed",
        example = "657dc70fc773a63db12e5f97",
        required = false
    )
    private String targetIntegration;

    @Schema(
        description = "The operator to be used in the operation",
        example = "ADD",
        required = false
    )
    private MetricOperationEnum operator;

    @Schema(
        description = "The criteria to be used in the operation, specified as key-value pairs",
        example = "{\"data_Publikation\": \"Berlingske\", \"data_Type\": \"Aktivt salg\"}",
        required = false
    )
    @ArraySchema(schema = @Schema(implementation = Criteria.class))
    @Valid
    private Map<String, String> criteria;
}
