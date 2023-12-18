package com.aau.p3.performancedashboard.converter;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.metricBuilder.MetricOperation;
import com.aau.p3.performancedashboard.model.Metric;
import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class MetricConverter {

    private static final Logger logger = LogManager.getLogger(MetricConverter.class);

    public MetricResponse convertToResponse(Metric metric) {
        logger.debug("Converting Metric to MetricResponse.");
        logger.debug("Metric ID: " + metric.getId());
        logger.debug("Metric Name: " + metric.getName());
        logger.debug("Metric DependentIntegrationIds: " + metric.getDependentIntegrationIds());

        MetricResponse metricResponse = new MetricResponse();
        metricResponse.setId(metric.getId());
        metricResponse.setName(metric.getName());
        metricResponse.setDependentIntegrationIds(metric.getDependentIntegrationIds());

        logger.debug("Converted MetricResponse: " + metricResponse);
        return metricResponse;
    }

    public Metric convertToEntity(CreateMetricRequest createMetricRequest) {
        logger.debug("Converting CreateMetricRequest to Metric.");
        logger.debug("Request Name: " + createMetricRequest.getName());
        logger.debug("Request MetricOperations: " + createMetricRequest.getMetricOperations());

        Metric metric = new Metric();
        metric.setName(createMetricRequest.getName());

        Set<String> dependentIntegrationIds = new HashSet<>();
        for (MetricOperation operation : createMetricRequest.getMetricOperations()) {
            logger.debug("Operation TargetIntegration: " + operation.getTargetIntegration());
            if (operation.getTargetIntegration() != null) {
                dependentIntegrationIds.add(operation.getTargetIntegration());
            }
        }

        metric.setMetricOperations(createMetricRequest.getMetricOperations());
        metric.setDependentIntegrationIds(new ArrayList<>(dependentIntegrationIds));

        logger.debug("Converted Metric: " + metric);
        return metric;
    }
}
