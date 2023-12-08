package com.aau.p3.performancedashboard.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String title;
    private String status;
    private List<String> messages;

    public ErrorResponse(String title, String status, String message) {
        this.title = title;
        this.status = status;
        this.messages.add(message);
    }

}
