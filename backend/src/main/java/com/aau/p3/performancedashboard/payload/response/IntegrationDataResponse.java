package com.aau.p3.performancedashboard.payload.response;

import java.util.Date;
import java.util.Map;

import lombok.Data;

/**
 * Represents IntegrationData returned to a client.
 */
@Data
public class IntegrationDataResponse {
    private String id;
    private String integrationId;
    private Date timestamp;
    private Map<String, Object> data;
}
