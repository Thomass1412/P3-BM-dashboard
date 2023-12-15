package com.aau.p3.performancedashboard.payload.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Data
@NoArgsConstructor
public class RegisterRequest {
  //@NotEmpty
  //@Size(min = 3, max = 20)
  @Schema(name = "username", example ="thejohndoe", required = true, description = "Username")
  private String username;

  //@NotEmpty
  //@Size(min = 3, max = 20)
  @Schema(name = "displayName", example ="John Doe", required = true, description = "Display name")
  private String displayName;

  @JsonIgnore
  public String getLogin() {
    return this.username;
  }

  //@NotEmpty
  //@Size(max = 50)
  //@Email
  @Schema(name = "email", example ="john@example.com", required = true, description = "Email")
  private String email;

  @Schema(name = "authorities", example ="[\"ROLE_ADMIN\", \"ROLE_SUPERVISOR\", \"ROLE_TV\"]", required = false, description = "Authorities. Defaults to ROLE_AGENT")
  private List<String> authorities;

  //@NotEmpty
  //@Size(min = 6, max = 40)
  @Schema(name = "password", example ="123456789", required = true, description = "Password")
  private String password;
}
