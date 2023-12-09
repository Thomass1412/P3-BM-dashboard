package com.aau.p3.performancedashboard.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;
import com.aau.p3.performancedashboard.payload.response.IntegrationResponse;
import com.aau.p3.performancedashboard.payload.response.PageableResponse;
import com.aau.p3.performancedashboard.service.IntegrationDataService;
import com.aau.p3.performancedashboard.service.IntegrationService;

import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// SWAGGER dependencies
// DOCS https://docs.swagger.io/swagger-core/v2.0.0-RC3/apidocs/io/swagger/v3/oas/annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

/**
 * This class is responsible for handling HTTP requests related to integrations
 * (plural).
 * It uses the {@link IntegrationService} to interact with the data layer.
 * It uses the {@link Autowired} annotation to inject the service dependency.
 * It uses the {@link RestController} annotation to indicate that it's a RESTful
 * web service controller.
 * It uses the {@link RequestMapping} annotation to map HTTP requests to handler
 * methods.
 */
@Tag(name = "Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integrations/pageable")
class IntegrationsController {

    // Service to interact with the data layer.
    private IntegrationService integrationService;

    /**
     * This constructor is used to inject the {@link IntegrationService} and
     * {@link IntegrationDataService} dependencies.
     *
     * @param integrationService     The service to interact with the data layer.
     * @param integrationDataService The service to interact with the integration
     *                               data.
     */
    @Autowired
    public IntegrationsController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * Fetches a page of integrations.
     * 
     * This endpoint retrieves a page of instantiated integrations based on the
     * provided page number and size. The response object will inherit from a
     * specific integration subclass, so fields may vary.
     * 
     * @param page The page number to retrieve (0-indexed).
     * @param size The number of integrations per page.
     * @return A {@link Mono} containing a {@link Page} of {@link Integration}
     *         objects.
     * 
     * @response 200 Successfully retrieved a page of integrations. The response
     *           body contains a JSON array of integrations.
     * @response 500 Internal Server Error. An error occurred while trying to fetch
     *           the integrations.
     */
    @Operation(summary = "Retrieve a page of instantiated integrations", description = "Fetches a page of integrations based on the provided page number and size. The response object will inherit from a specific integration subclass, so fields may vary.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a page of integrations", content = {
                    @Content(schema = @Schema(implementation = PageableResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An error occurred while trying to fetch the integrations.")
    })
    @GetMapping(produces = "application/json")
    public Mono<Page<Integration>> getAllIntegrationsBy(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        // Extract the request parameters into a Pageable object.
        Pageable pageable = PageRequest.of(page, size);
        // Fetch the page of integrations.
        return integrationService.findAllBy(pageable);
    }
}

/**
 * This class is responsible for handling HTTP requests related to integration
 * (singular).
 * It uses the {@link IntegrationService} to interact with the data layer.
 * It uses the {@link Autowired} annotation to inject the service dependency.
 * It uses the {@link RestController} annotation to indicate that it's a RESTful
 * web service controller.
 * It uses the {@link RequestMapping} annotation to map HTTP requests to handler
 * methods.
 */
@Tag(name = "Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationController {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationController.class);

    // Service to interact with the data layer.
    private IntegrationService integrationService;
    private IntegrationDataService integrationDataService;

    /**
     * This constructor is used to inject the {@link IntegrationService} and
     * {@link IntegrationDataService} dependencies.
     *
     * @param integrationService     The service to interact with the data layer.
     * @param integrationDataService The service to interact with the integration
     *                               data.
     */
    @Autowired
    public IntegrationController(IntegrationService integrationService, IntegrationDataService integrationDataService) {
        this.integrationService = integrationService;
        this.integrationDataService = integrationDataService;
    }

    @Operation(summary = "Instantiate a new integration", description = "The request body must include a unique name and a predefined type. ['internal']")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = IntegrationResponse.class)), mediaType = "application/json") }, description = "Successfully created a new integration"),
            @ApiResponse(responseCode = "400", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)), mediaType = "application/json") }, description = "Bad request. Invalid parameters or requested integration already exists."),
            @ApiResponse(responseCode = "404", description = "Integration type not found."),
            @ApiResponse(responseCode = "500", description = "Generic Internal Server Error."),
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Mono<IntegrationResponse>> createIntegration(
            @RequestBody @Valid CreateIntegrationRequest createIntegrationRequest) {
        return ResponseEntity.ok().body(integrationService.createIntegration(createIntegrationRequest));
    }

    @Operation(summary = "Submit new integrationData", description = "The request body must include valid integrationData and path variable must contain an existing integrationId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = IntegrationDataResponse.class)), mediaType = "application/json") }, description = "Successfully created new integrationData"),
            @ApiResponse(responseCode = "400", description = "Bad request. Missing integrationData"),
            @ApiResponse(responseCode = "404", description = "Integration with id not found"),
            @ApiResponse(responseCode = "500"),
    })

    @PostMapping("/{integrationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Mono<IntegrationDataResponse>> submitIntegrationData(@PathVariable String integrationId,
            @RequestBody @Valid CreateIntegrationDataRequest integrationDataRequest) throws Exception {
        return ResponseEntity.ok()
                .body(integrationDataService.saveIntegrationData(integrationId, integrationDataRequest));
    }

    @Operation(summary = "Retrieve an integration by its ID", description = "Fetches a single integration based on its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Integration.class)), description = "Successfully retrieved the integration"),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)), description = "Integration not found")
    })
    @GetMapping("/{integrationId}") // This must be converted to use IntegrationResponse
    public Mono<ResponseEntity<Integration>> getIntegrationById(@PathVariable String integrationId) {
        return integrationService.findById(integrationId)
                .map(integration -> ResponseEntity.ok().body(integration));
    }

   @GetMapping("/{integrationId}/data/pageable")
    public Mono<Page<IntegrationDataResponse>> getAllIntegrationDataBy(@PathVariable String integrationId, @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        // Extract the request parameters into a Pageable object.
        Pageable pageable = PageRequest.of(page, size);
        // Fetch the page of integrations.
        return integrationDataService.findAllBy(integrationId, pageable);
    }

    @GetMapping("/{integrationId}/schema")
    public ResponseEntity<Mono<Document>> getSchemaBy(@PathVariable String integrationId) {
        return ResponseEntity.ok().body(integrationService.getSchemaBy(integrationId));
    }

    @ResponseBody
    @ExceptionHandler(IntegrationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(IntegrationNotFoundException ex) {
        ErrorResponse response = new ErrorResponse("Integration not found", "error", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), "error", "Internal Server Error");
        return ResponseEntity.internalServerError().body(response);
    }

    @ResponseBody
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectResultSizeDataAccessException(
            IncorrectResultSizeDataAccessException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), "error", "Bad Request");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
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

        ErrorResponse response = new ErrorResponse("Validation Error", "error", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles the exception thrown when there is a write error in MongoDB.
     * Extracts the details of the error and constructs a custom response with the
     * error message and code.
     * Returns a ResponseEntity with a bad request status and the custom response as
     * the body.
     *
     * @param ex The MongoWriteException that was thrown
     * @return A ResponseEntity containing a Mono of ErrorResponse
     */
    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleMongoWriteException(MongoWriteException ex) {
        WriteError writeError = ex.getError();
        String message = writeError.getMessage();
        int code = writeError.getCode();
        BsonDocument details = writeError.getDetails();

        List<String> errors = new ArrayList<>();
        if (details != null) {
            errors.addAll(extractErrors(details));
        } else {
            errors.add(message);
        }

        String customMessage = String.format("Error writing to MongoDB. Code: %d.", code);
        ErrorResponse response = new ErrorResponse(customMessage, message, errors);
        return ResponseEntity.badRequest().body(response);
    }

    private List<String> extractErrors(BsonValue value) {
        List<String> errors = new ArrayList<>();
        if (value.isDocument()) {
            BsonDocument document = value.asDocument();
            if (document.containsKey("required")) {
                for (BsonValue field : document.getArray("required")) {
                    errors.add(String.format("Missing required field: %s", field.asString().getValue()));
                }
            }
            if (document.containsKey("properties")) {
                for (BsonValue property : document.getDocument("properties").values()) {
                    errors.addAll(extractErrors(property));
                }
            }
            if (document.containsKey("type") && document.getString("type").getValue().equals("number")) {
                String consideredValue = document.getString("consideredValue").getValue();
                String consideredType = document.getString("consideredType").getValue();
                errors.add(String.format("Type mismatch: Provided value: %s. Provided type: %s. Expected type: number.",
                        consideredValue, consideredType));
            }
        } else if (value.isArray()) {
            for (BsonValue element : value.asArray()) {
                errors.addAll(extractErrors(element));
            }
        }
        return errors;
    }
}
