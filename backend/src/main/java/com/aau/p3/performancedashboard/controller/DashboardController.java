package com.aau.p3.performancedashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.payload.request.CreateDashboardRequest;
import com.aau.p3.performancedashboard.payload.response.DashboardResponse;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboards", description = "Dashboard management APIs")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    // Logger
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(DashboardController.class);
    
    // Dependencies
    private final DashboardService dashboardService;

    // Constructor injection
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Create a new dashboard", description = "Creates a new dashboard with the provided details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully created the dashboard", content = {
            @Content(schema = @Schema(implementation = DashboardResponse.class), mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "400", description = "Bad request. Dashboard creation failed.", content = {
            @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "403", description = "Access denied", content = {
            @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
        })
    })
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public Mono<ResponseEntity<DashboardResponse>> createDashboard(@Valid @RequestBody CreateDashboardRequest createDashboardRequest) {
        return this.dashboardService.createDashboard(createDashboardRequest)
                .map(dashboardResponse -> ResponseEntity.ok(dashboardResponse));
    }
}