package com.aau.p3.performancedashboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResultResponse;
import com.aau.p3.performancedashboard.payload.response.PageableResponse;
import com.aau.p3.performancedashboard.service.MetricService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

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

    @Operation(summary = "Retrieve all metrics by a pageable request", description = "Retrieves all metrics by a pageable request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the metrics", content = {
                    @Content(schema = @Schema(implementation = PageableResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping(path = "/pageable", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public Mono<Page<MetricResponse>> getMetricsBy(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        Instant start = Instant.now(); // Record the start time
        Pageable pageable = PageRequest.of(page, size);
        return metricService.findAllBy(pageable)
                .doOnNext(pageResponse -> {
                    // Log the time taken to process the request
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Processed get metrics by page request in " + duration.toMillis()
                            + " ms");
                })
                .doOnTerminate(() -> {
                    // This will be called on completion or error
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Total time taken for get metrics by page request: "
                            + duration.toMillis() + " ms");
                });

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
        Instant start = Instant.now(); // Record the start time

        return metricService.createMetric(createMetricRequest)
                .map(response -> {
                    // Log the time taken to process the request
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Processed create metric request in " + duration.toMillis()
                            + " ms");
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnTerminate(() -> {
                    // This will be called on completion or error
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Total time taken for create metric request: "
                            + duration.toMillis() + " ms");
                });
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
        Instant start = Instant.now(); // Record the start time
        logger.debug("Received request to delete metric");

        return metricService.deleteMetric(metricId)
                .map(response -> {
                    // Log the time taken to process the request
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Processed delete metric request in " + duration.toMillis()
                            + " ms");
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnTerminate(() -> {
                    // This will be called on completion or error
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Total time taken for delete metric request: "
                            + duration.toMillis() + " ms");
                });
    }

    @Operation(summary = "Calculate a metric", description = "Calculates a metric based on the provided metric ID, start date, and end date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully calculated the metric", content = {
                    @Content(schema = @Schema(implementation = MetricResultResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Metric not found", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping(path = "/calculate/{metricId}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public Mono<ResponseEntity<MetricResultResponse>> calculateMetric(
            @PathVariable(value = "metricId") @Parameter(description = "ID of the metric to calculate", example = "605c147c4f7d4a4b3c77ba92") String metricId,
            @RequestParam @Parameter(description = "Start date for the calculation", example = "2022-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @Parameter(description = "End date for the calculation", example = "2022-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam @Parameter(description = "Limit the number of returned users. 0 means no limit", example = "10") Integer resultLimit,
            @RequestParam @Parameter(description = "Sort order for the returned count values. Valid arguments are 'ASC' and 'DESC'. Default is 'ASC'.", example = "ASC") String sortOrder) {

        Instant start = Instant.now(); // Record the start time
        logger.debug("Received request to calculate metric");

        return metricService.calculateMetric(metricId, startDate, endDate, resultLimit, sortOrder)
                .map(response -> {
                    // Log the time taken to process the request
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Processed calculate metric request in " + duration.toMillis()
                            + " ms");
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnTerminate(() -> {
                    // This will be called on completion or error
                    Duration duration = Duration.between(start, Instant.now());
                    logger.debug("Total time taken for calculate metric request: "
                            + duration.toMillis() + " ms");
                });
    }
}
