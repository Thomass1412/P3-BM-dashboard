package com.aau.p3.performancedashboard.exceptions;

import lombok.NoArgsConstructor;

/**
 * Exception thrown when an integration is not found.
 */
@NoArgsConstructor
public class IntegrationNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new IntegrationNotFoundException with the specified detail message.
     * @param message the detail message
     */
    public IntegrationNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new IntegrationNotFoundException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public IntegrationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
