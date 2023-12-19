package com.aau.p3.performancedashboard.payload.response;

import java.util.List;

import com.aau.p3.performancedashboard.dashboard.DashboardWidget;

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
    name = "DashboardResponse",
    description = "A dashboard response containing a collection of widgets"
)
public class DashboardResponse {
    
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Schema(
        name = "name",
        description = "Name of the dashboard",
        example = "Sales & Retention",
        required = true)
    private String name;

    private  List<DashboardWidget> widgets;
}
