// this is just so we can remember how to make auth requests in the future
// to secure an endpoint, 
package com.aau.p3.performancedashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import reactor.core.publisher.Mono;


@SecurityScheme(
    name = "bearerAuth", 
    type = SecuritySchemeType.HTTP, 
    scheme = "bearer", 
    bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication Test Controller", description = "Allows to test different endpoints with different authorization levels")
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  
  @Operation(summary = "Test endpoint for all users", description = "Test endpoint for all users")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully authorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
  })
  @GetMapping("/all")
  public Mono<String> allAccess() {
    return Mono.just("Public Content.");
  }

  @Operation(summary = "Test endpoint for agents", description = "Test endpoint for agents")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully authorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
  })
  @GetMapping("/agent")
  @PreAuthorize("hasRole('AGENT') or hasRole('SUPERVISOR') or hasRole('ADMIN')")
  public Mono<String> userAccess() {
    return Mono.just("agent Content.");
  }

  @Operation(summary = "Test endpoint for supervisors", description = "Test endpoint for supervisors")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully authorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
  })
  @GetMapping("/supervisor")
  @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
  public Mono<String> moderatorAccess() {
    return Mono.just("supervisor Board.");
  }

  @Operation(summary = "Test endpoint for admins", description = "Test endpoint for admins")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully authorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    }),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
  })
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<String> adminAccess() {
    return Mono.just("Admin Board.");
  }
}