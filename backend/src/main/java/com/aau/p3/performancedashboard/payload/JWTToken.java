package com.aau.p3.performancedashboard.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a JSON Web Token (JWT) used for authentication and authorization.
 */
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTToken {
    private String token;
    
}
