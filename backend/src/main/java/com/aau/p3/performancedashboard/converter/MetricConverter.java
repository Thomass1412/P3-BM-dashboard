package com.aau.p3.performancedashboard.converter;

import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.Metric;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;

@Component
public class MetricConverter {
    
    public MetricResponse convertToResponse(Metric metric) {
        MetricResponse metricResponse = new MetricResponse();
        metricResponse.setId(metric.getId());
        metricResponse.setName(metric.getName());
        metricResponse.setDependentIntegrationIds(metric.getDependentIntegrationIds());
        return metricResponse;
    }
}
