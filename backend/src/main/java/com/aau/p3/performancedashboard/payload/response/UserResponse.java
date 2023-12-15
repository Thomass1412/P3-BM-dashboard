package com.aau.p3.performancedashboard.payload.response;

import java.util.List;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {
    

    @Schema(name = "id", example = "657b7641d0ec946a2b94051d")
    private String id;
    
    @NotBlank(message = "Login cannot be blank")
    @Schema(name = "login", example = "1ors")
    private String login;

    @NotBlank(message = "Display name cannot be blank")
    @Schema(name = "displayName", example = "John Doe")
    private String displayName;

    @NotBlank(message = "Email cannot be blank")
    @Schema(name = "email", example = "example@example.org")
    private String email;

    private List<String> achievements;

    @Schema(name = "authorities", example = "ROLE_USER")
    private List<String> authorities;
}
