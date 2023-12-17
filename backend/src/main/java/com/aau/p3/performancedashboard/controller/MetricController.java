package com.aau.p3.performancedashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;
import com.aau.p3.performancedashboard.service.MetricService;

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
@Tag(name = "Metrics", description = "Metric management APIs")
@RestController
@RequestMapping("/api/v1/metric")
public class MetricController {

        // Logger
        private static final Logger logger = LogManager.getLogger(MetricController.class);

        // Dependencies
        private final MetricService metricService;

        // Constructor injection
        public MetricController(MetricService metricService) {
                this.metricService = metricService;
        }

        /**
         * Creates a new metric.
         *
         * @param registerRequest the register request containing metric information
         * @return a Mono of ResponseEntity containing the metric response
         */
        @Operation(summary = "Create a new metric", description = "Creates a new metric with the provided details.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully created the metric", content = {
                                        @Content(schema = @Schema(implementation = MetricResponse.class), mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request. User registration failed.", content = {
                                        @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = {
                                        @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
                        })
        })
        @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
        @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
        public Mono<ResponseEntity<MetricResponse>> createMetric(
                        @Valid @RequestBody CreateMetricRequest createMetricRequest) {
                logger.debug("Received request to create metric");
                return metricService.createMetric(createMetricRequest)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Delete a metric", description = "Deletes a metric with the provided id.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted the metric", content = {
                                        @Content(schema = @Schema(implementation = MessageResponse.class), mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "400", description = "Bad request. Metric deletion failed.", content = {
                                        @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
                        }),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = {
                                        @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
                        })
        })
        @DeleteMapping(path = "/{metricId}", produces = "application/json")
        @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
        public Mono<ResponseEntity<MessageResponse>> deleteMetric(@PathVariable(value = "metricId") String metricId) {
                logger.debug("Received request to delete metric");
                return metricService.deleteMetric(metricId)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }
}
