package com.aau.p3.performancedashboard.payload.response;

import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.Setter;

/**
 * Represents IntegrationData returned to a client.
 */
@Data
@Setter
public class IntegrationDataResponse {
    private String id;
    private String integrationId;
    private String userId;
    private Date timestamp;
    private Map<String, Object> data;
}
