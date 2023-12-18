package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "WidgetOptions",
    description = "Configuration options for a dashboard widget"
)
public class WidgetOptions {

    @NotBlank(message = "Integration id must not be empty")
    @Schema(
        name = "integrationId",
        description = "Unique identifier of the integration",
        example = "655a40f451a6f25c2b736e30",
        required = true
    )
    private String integrationId;

    @NotBlank(message = "Metric id must not be empty")
    @Schema(
        name = "metricId",
        description = "Unique identifier of the metric",
        example = "655a40f451a6f25c2b736e30",
        required = true
    )
    private String metricId;

    @NotNull(message = "Sorted by must not be empty")
    @Schema(
        name = "sortedBy",
        description = "Criteria for sorting the results",
        example = "{\"userId\":\"asc\"}",
        required = true
    )
    private Map<String, String> sortedBy;

    @NotNull(message = "Start date must not be empty") 
    @Schema(
        name = "startDate",
        description = "Start date for the metric calculation",
        example = "2021-05-01T00:00:00.000Z",
        required = true
    )
    private Date startDate;

    @NotNull(message = "End date must not be empty")
    @Schema(
        name = "endDate",
        description = "End date for the metric calculation",
        example = "2021-05-01T00:00:00.000Z",
        required = true
    )
    private Date endDate;

    @NotNull(message = "Limit must not be empty")
    @Schema(
        name = "limit",
        description = "Limit the amount of users to retrieve",
        example = "10",
        required = true
    )
    private Integer limit;
}
