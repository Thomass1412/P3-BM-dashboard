package com.aau.p3.performancedashboard.model;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.aau.p3.performancedashboard.dashboard.DashboardWidget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
    name = "Dashboard",
    description = "A dashboard containing a collection of widgets"
)
public class Dashboard {
    
    @Id
    @Schema(
        name = "id",
        description = "Unique identifier of the dashboard",
        example = "605c147c4f7d4a4b3c77ba92",
        required = true
    )
    private String id;

    @NotBlank(message = "Name must not be empty")
    @Schema(
        name = "name",
        description = "Name of the dashboard",
        example = "Sales & Retention",
        required = true
    )
    private String name;

    @Schema(
        name = "widgets",
        description = "List of widgets contained in the dashboard",
        example = "[{\"name\":\"Sales Today\",\"type\":\"LEADERBOARD\",\"options\":{\"integrationId\":\"655a40f451a6f25c2b736e30\",\"metricId\":\"655a40f451a6f25c2b736e30\",\"sortedBy\":{\"userId\":\"asc\"},\"startDate\":\"2021-05-01T00:00:00.000Z\",\"endDate\":\"2021-05-01T00:00:00.000Z\",\"limit\":10}}]",
        required = true
    )
    private List<DashboardWidget> widgets;
}
