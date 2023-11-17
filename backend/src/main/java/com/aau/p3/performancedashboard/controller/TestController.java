// this is just so we can remember how to make auth requests in the future
// to secure an endpoint, 
package com.aau.p3.performancedashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('AGENT') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "agent Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String moderatorAccess() {
    return "supervisor Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }
}
