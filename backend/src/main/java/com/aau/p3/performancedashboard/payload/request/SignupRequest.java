package com.aau.p3.performancedashboard.payload.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  @Schema(name = "username", example ="thejohndoe", required = true, description = "Username")
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  @Schema(name = "email", example ="john@example.com", required = true, description = "Email")
  private String email;

  @Schema(name = "roles", example ="[\"admin\", \"supervisor\", \"tv\"]", required = false, description = "Roles. Defaults to agent")
  private List<String> roles;

  @NotBlank
  @Size(min = 6, max = 40)
  @Schema(name = "password", example ="shuush", required = true, description = "Password")
  private String password;
}
