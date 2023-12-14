// this is just so we can remember how to make auth requests in the future
// to secure an endpoint, 
package com.aau.p3.performancedashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import reactor.core.publisher.Mono;


@SecurityScheme(
    name = "bearerAuth", 
    type = SecuritySchemeType.HTTP, 
    scheme = "bearer", 
    bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  @GetMapping("/all")
  public Mono<String> allAccess() {
    return Mono.just("Public Content.");
  }

  @GetMapping("/agent")
  @PreAuthorize("hasRole('AGENT') or hasRole('SUPERVISOR') or hasRole('ADMIN')")
  public Mono<String> userAccess() {
    return Mono.just("agent Content.");
  }

  @GetMapping("/supervisor")
  @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
  public Mono<String> moderatorAccess() {
    return Mono.just("supervisor Board.");
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<String> adminAccess() {
    return Mono.just("Admin Board.");
  }
}