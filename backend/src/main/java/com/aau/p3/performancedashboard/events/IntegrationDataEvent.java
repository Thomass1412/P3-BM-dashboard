package com.aau.p3.performancedashboard.events;

import java.util.EventObject;

import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;

import lombok.Setter;

@Setter
public class IntegrationDataEvent extends EventObject {
    private final String integrationId;
    private final CreateIntegrationDataRequest integrationDataRequest;

    public IntegrationDataEvent(Object source, String integrationId, CreateIntegrationDataRequest integrationDataRequest) {
        super(source);
        this.integrationId = integrationId;
        this.integrationDataRequest = integrationDataRequest;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public CreateIntegrationDataRequest getIntegrationDataRequest() {
        return integrationDataRequest;
    }
}
