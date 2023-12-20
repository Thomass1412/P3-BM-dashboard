package com.aau.p3.performancedashboard.payload.request;

import java.util.List;

import com.aau.p3.performancedashboard.dashboard.DashboardWidget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDashboardRequest {

    @NotBlank(message = "Name cannot be blank")
    @Schema(name = "name", description = "Name of the dashboard", example = "Sales & Retention", required = true)
    private String name;

    @Valid
    @Schema(name = "widgets", description = "Widgets to be displayed on the dashboard", required = true)
    private List<DashboardWidget> widgets;
}

