package com.aau.p3.performancedashboard.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class LoginRequest {
  @NotEmpty
  @Size(min = 3, max = 50)
  private String username;

  @NotEmpty
  private String password;

}
