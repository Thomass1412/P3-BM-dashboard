package com.aau.p3.performancedashboard.controller;


public class CustomResponse {
    private String message;
    private String status;
    private String error;

    // Default constructor
    public CustomResponse() {
    }

    // Constructor with all fields
    public CustomResponse(String message, String status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
