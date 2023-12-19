package com.aau.p3.performancedashboard.exceptions;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Exception thrown when an integration is not found.
 */
@NoArgsConstructor
public class IntegrationNotFoundException extends RuntimeException {

    @Getter
    private List<String> integrationIds;
    
    /**
     * Constructs a new IntegrationNotFoundException with the specified detail message.
     * @param message the detail message
     */
    public IntegrationNotFoundException(String message) {
        super(message);
    }

    public IntegrationNotFoundException(List<String> integrationIds) {
        super("Integration with id(s) " + integrationIds + " not found");
        this.integrationIds = integrationIds;
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
