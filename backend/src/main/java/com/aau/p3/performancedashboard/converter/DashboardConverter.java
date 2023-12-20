package com.aau.p3.performancedashboard.converter;

import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.Dashboard;
import com.aau.p3.performancedashboard.payload.request.CreateDashboardRequest;
import com.aau.p3.performancedashboard.payload.response.DashboardResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class DashboardConverter {
    
    // Logger
    private static final Logger logger = LogManager.getLogger(DashboardConverter.class);

    /**
        * Converts a Dashboard object to a DashboardResponse object.
        *
        * @param dashboard the Dashboard object to be converted
        * @return the converted DashboardResponse object
        */
    public DashboardResponse convertToResponse(Dashboard dashboard) {
        logger.debug("Converting Dashboard to DashboardResponse.");
        logger.debug("Dashboard ID: " + dashboard.getId());
        logger.debug("Dashboard Name: " + dashboard.getName());
        logger.debug("Dashboard Widgets: " + dashboard.getWidgets());

        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setId(dashboard.getId());
        dashboardResponse.setName(dashboard.getName());
        dashboardResponse.setWidgets(dashboard.getWidgets());

        logger.debug("Converted DashboardResponse: " + dashboardResponse);
        return dashboardResponse;
    }

    /**
        * Converts a CreateDashboardRequest object to a Dashboard object.
        *
        * @param createDashboardRequest The CreateDashboardRequest object to be converted.
        * @return The converted Dashboard object.
        */
    public Dashboard convertToEntity(CreateDashboardRequest createDashboardRequest) {
        logger.debug("Converting CreateDashboardRequest to Dashboard.");
        logger.debug("Request Name: " + createDashboardRequest.getName());
        logger.debug("Request Widgets: " + createDashboardRequest.getWidgets());

        Dashboard dashboard = new Dashboard();
        dashboard.setName(createDashboardRequest.getName());
        dashboard.setWidgets(createDashboardRequest.getWidgets());

        logger.debug("Converted Dashboard: " + dashboard);
        return dashboard;
    }
}
