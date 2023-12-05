package com.aau.p3.performancedashboard.controller;

import com.aau.p3.performancedashboard.dto.CustomResponse;
import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.service.IntegrationService;

// SWAGGER dependencies
// DOCS https://docs.swagger.io/swagger-core/v2.0.0-RC3/apidocs/io/swagger/v3/oas/annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.IntegrationData;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class is responsible for handling HTTP requests related to integrations (plural).
 * It uses the {@link IntegrationService} to interact with the data layer.
 * It uses the {@link Autowired} annotation to inject the service dependency.
 * It uses the {@link RestController} annotation to indicate that it's a RESTful web service controller.
 * It uses the {@link RequestMapping} annotation to map HTTP requests to handler methods.
 */
@Tag(name = "Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integrations")
class IntegrationsController {

    // Service to interact with the data layer.
    private IntegrationService integrationService;

    /**
     * This constructor is used to inject the {@link IntegrationService} dependency.
     *
     * @param integrationService The service to interact with the data layer.
     */
    @Autowired
    public IntegrationsController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * Fetches all integrations.
     * 
     * This endpoint retrieves all instantiated integrations. The response object will inherit from a specific integration subclass, so fields may vary.
     * 
     * @return A {@link ResponseEntity} containing a {@link Flux} of {@link Integration} objects.
     * 
     * @response 200 Successfully retrieved all integrations. The response body contains a JSON array of integrations.
     * @response 500 Internal Server Error. An error occurred while trying to fetch the integrations.
     */
    @Operation(summary = "Retrieve all instantiated integrations", description = "Fetches all integrations. The response object will inherit from a specific integration subclass, so fields may vary.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all integrations", content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = Integration.class)), mediaType = "application/json")
        }),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An error occurred while trying to fetch the integrations.")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Flux<Integration>> getAllIntegrations() {
        return ResponseEntity.ok().body(integrationService.findAll());
    }
}

/**
 * This class is responsible for handling HTTP requests related to integration (singular).
 * It uses the {@link IntegrationService} to interact with the data layer.
 * It uses the {@link Autowired} annotation to inject the service dependency.
 * It uses the {@link RestController} annotation to indicate that it's a RESTful web service controller.
 * It uses the {@link RequestMapping} annotation to map HTTP requests to handler methods.
 */
@Tag(name = "Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationController {

    // Service to interact with the data layer.
    private IntegrationService integrationService;

    /**
     * This constructor is used to inject the {@link IntegrationService} dependency.
     *
     * @param integrationService The service to interact with the data layer.
     */
    @Autowired
    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }
    
    @Operation(summary = "Instantiate a new integration", description = "The request body must include an unique name and a predefined type. \\['internal'\\]")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = Integration.class)), mediaType = "application/json") }, description = "Successfully created new integration"),
            @ApiResponse(responseCode = "400", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = CustomResponse.class)), mediaType = "application/json") }, description = "Bad request. Invalid parameters or requested integration already exists."),
            @ApiResponse(responseCode = "404", description = "Integration type not found."),
            @ApiResponse(responseCode = "500", description = "Generic Internal Server Error."),
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Mono<Integration>> createIntegration(@RequestBody @Valid IntegrationDTO integrationDTO)
            throws Exception {
        return ResponseEntity.ok()
                .body(integrationService.saveIntegration(integrationDTO));
    }

    @Operation(summary = "Instantiate new integrationData", description = "The request body must include valid integrationData and path variable must contain an existing integrationId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = IntegrationData.class)), mediaType = "application/json") }, description = "Successfully created new integrationData"),
            @ApiResponse(responseCode = "400", description = "Bad request. Missing integrationData"),
            @ApiResponse(responseCode = "404", description = "Integration with id not found"),
            @ApiResponse(responseCode = "500"),
    })

    @PostMapping("/{integrationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IntegrationData> createIntegrationData(@PathVariable String integrationId,
            @RequestBody IntegrationData integrationData) throws Exception {
        Integration integration = integrationService.findById(integrationId).block();
        Mono<IntegrationData> res = integration.saveIntegrationData(integrationData);
        return res;
    }

    @Operation(summary = "Retrieve an integration by its ID", description = "Fetches a single integration based on its unique ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Integration.class)), description = "Successfully retrieved the integration"),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = CustomResponse.class)), description = "Integration not found")
    })
    @GetMapping("/{integrationId}")
    public Mono<ResponseEntity<Integration>> getIntegrationById(@PathVariable String integrationId) {
        return integrationService.findById(integrationId)
            .map(integration -> ResponseEntity.ok().body(integration));
    }

    @ResponseBody
    @ExceptionHandler(IntegrationNotFoundException.class)
    public ResponseEntity<CustomResponse> handleNotFoundException(IntegrationNotFoundException ex) {
        CustomResponse response = new CustomResponse("Integration not found", "error", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleException(Exception ex) {
        CustomResponse response = new CustomResponse(ex.getMessage(), "error", "Internal Server Error");
        return ResponseEntity.internalServerError().body(response);
    }

    @ResponseBody
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<CustomResponse> handleIncorrectResultSizeDataAccessException(
            IncorrectResultSizeDataAccessException ex) {
        CustomResponse response = new CustomResponse(ex.getMessage(), "error", "Bad Request");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage();
                    }
                })
                .collect(Collectors.toList());

        CustomResponse response = new CustomResponse("Validation Error", "error", errors);
        return ResponseEntity.badRequest().body(response);
    }
}
