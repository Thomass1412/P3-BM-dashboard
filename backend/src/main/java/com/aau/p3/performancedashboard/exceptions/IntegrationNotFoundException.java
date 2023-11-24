package com.aau.p3.performancedashboard.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IntegrationNotFoundException extends RuntimeException {
    
    public IntegrationNotFoundException(String message) {
        super(message);
    }
}
