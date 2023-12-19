package com.aau.p3.performancedashboard.payload.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "DashboardCalulatedResponse",
    description = "A dashboard calculated response containing a collection of calculated metrics"
)
public class DashboardCalulatedResponse {
    
    @Schema(
        name = "id",
        description = "ID of the dashboard",
        example = "123456",
        required = true)
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Schema(
        name = "name",
        description = "Name of the dashboard",
        example = "Sales & Retention",
        required = true)
    private String name;

    @Schema(
        name = "calculatedMetrics",
        description = "List of calculated metrics for the dashboard",
        required = true)
    private List<MetricResultResponse> calculatedMetrics;
}
