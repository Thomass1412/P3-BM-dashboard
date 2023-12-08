package com.aau.p3.performancedashboard.payload.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateIntegrationDataRequest {

    private Date timestamp;

    @NotEmpty(message = "Data cannot be empty")
    Map<String, Object> data;

    public CreateIntegrationDataRequest(Date timestamp, Map<String, Object> data) {
        this.timestamp = timestamp != null ? timestamp : new Date();
        this.data = data;
    }
}
