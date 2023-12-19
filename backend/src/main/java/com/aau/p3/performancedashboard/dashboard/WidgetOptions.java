package com.aau.p3.performancedashboard.dashboard;

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

    
    @Schema(
        name = "startDateType",
        description = "Type of the start date interval",
        example = "THIS_WEEK",
        required = true
    )
    private OptionsDateIntervalEnum startDateType;
    
    @Schema(
        name = "startDateTypeIfCustom",
        description = "Custom start date if startDateType is CUSTOM",
        example = "2022-01-01",
        required = false
    )
    private Date startDateTypeIfCustom;
    
    @Schema(
        name = "endDateType",
        description = "Type of the end date interval",
        example = "THIS_WEEK",
        required = true
    )
    private OptionsDateIntervalEnum endDateType;
    
    @Schema(
        name = "endDateTypeIfCustom",
        description = "Custom end date if endDateType is CUSTOM",
        example = "2022-12-31",
        required = false
    )
    private Date endDateTypeIfCustom;

    @NotNull(message = "Limit must not be empty")
    @Schema(
        name = "limit",
        description = "Limit the amount of users to retrieve",
        example = "10",
        required = true
    )
    private Integer limit;
}
