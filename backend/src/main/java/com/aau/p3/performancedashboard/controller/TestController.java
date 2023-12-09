// this is just so we can remember how to make auth requests in the future
// to secure an endpoint, 
package com.aau.p3.performancedashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  @GetMapping("/all")
  public Mono<String> allAccess() {
    return Mono.just("Public Content.");
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('AGENT') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public Mono<String> userAccess() {
    return Mono.just("agent Content.");
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public Mono<String> moderatorAccess() {
    return Mono.just("supervisor Board.");
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<String> adminAccess() {
    return Mono.just("Admin Board.");
  }
}