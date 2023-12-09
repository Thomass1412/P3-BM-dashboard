package com.aau.p3.performancedashboard.payload.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String title;
    private String status;
    private List<String> messages;

    /**
     * Constructs a new ErrorResponse object with the specified title, status, and
     * message.
     * 
     * @param title   the title of the error
     * @param status  the status code of the error
     * @param message the error message
     */
    public ErrorResponse(String title, String status, String message) {
        this.title = title;
        this.status = status;
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    /**
     * Constructs a new ErrorResponse object with the specified title, status, and
     * a list of messages.
     * 
     * @param title   the title of the error
     * @param status  the status code of the error
     * @param message the error message list
     */
    public ErrorResponse(String title, String status, List<String> messages) {
        this.title = title;
        this.status = status;
        this.messages = messages != null ? messages : new ArrayList<>();
    }
}
