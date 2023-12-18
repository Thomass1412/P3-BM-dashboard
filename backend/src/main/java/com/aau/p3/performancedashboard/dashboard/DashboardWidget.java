package com.aau.p3.performancedashboard.dashboard;

import com.aau.p3.performancedashboard.model.WidgetOptions;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
    name = "DashboardWidget",
    description = "A widget to be displayed on a dashboard"
)
public class DashboardWidget {

    @NotBlank(message = "Name must not be empty")
    @Schema(
        name = "name",
        description = "Name of the dashboard widget",
        example = "Salg i dag",
        required = true
    )
    private String name;

    @NotNull(message = "Type must not be empty")
    @Schema(
        name = "type",
        description = "Type of the dashboard widget",
        example = "LEADERBOARD",
        required = true
    )
    private DashboardWidgetTypeEnum type;

    @NotNull(message = "Options must not be empty")
    @Schema(
        name = "options",
        description = "Configuration options for the dashboard widget",
        example = "{\"integrationId\":\"655a40f451a6f25c2b736e30\",\"metricId\":\"655a40f451a6f25c2b736e30\",\"sortedBy\":{\"userId\":\"asc\"},\"startDate\":\"2021-05-01T00:00:00.000Z\",\"endDate\":\"2021-05-01T00:00:00.000Z\",\"limit\":10}",
        required = true
    )
    @Valid
    private WidgetOptions options;
}
