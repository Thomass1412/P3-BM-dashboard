package com.aau.p3.performancedashboard.payload.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateIntegrationDataRequest {

    @NotEmpty(message = "User ID cannot be empty")
    String userId;

    @Schema(description = "Timestamp of the data", example = "2021-05-01T12:00:00.000Z")
    private Date timestamp;

    @NotEmpty(message = "Data cannot be empty")
    @Schema(description = "Data for this specific integration", example = "{\"weather\": \"sunny\", \"humidity\": 50.0}")
    Map<String, Object> data;

    public CreateIntegrationDataRequest(Date timestamp, Map<String, Object> data) {
        this.timestamp = timestamp != null ? timestamp : new Date();
        this.data = data;
    }
}
