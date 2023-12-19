package com.aau.p3.performancedashboard.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.aau.p3.performancedashboard.metricBuilder.MetricOperation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "metrics")
public class Metric {
    
    @Id
    private String id;

    @NotBlank(message = "Name must not be empty")
    @Schema(name = "name", example = "Berlingske salg", required = true)
    private String name;

    @NotEmpty(message = "Dependent integration ids must not be empty")
    @Schema(name = "dependentIntegrationIds", example = "[\"655a40f451a6f25c2b736e30\"]", required = true)
    private List<String> dependentIntegrationIds;

    @Schema(name = "dependentMetricIds", example = "[\"655a40f451a6f25c2b736e30\"]", required = false)
    private List<String> dependentMetricIds;

    private List<MetricOperation> metricOperations;
}
