package com.aau.p3.performancedashboard.exceptions;

import lombok.NoArgsConstructor;

/**
 * Exception thrown when a resource is not found.
 */
@NoArgsConstructor
public class NotFoundException extends RuntimeException{

    /**
     * Exception thrown when a resource is not found.
     *
     * @param message The error message associated with the exception.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Exception thrown when a resource is not found.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
