package com.aau.p3.performancedashboard.controller;

import com.aau.p3.performancedashboard.dto.IntegrationDTO;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @Operation(summary = "Retrieve all instantiated integrations", description = "The response object will inherit from a specific integration subclass. Fields may vary.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = Integration.class)), mediaType = "application/json") }, description = "Successfully retrieved all integrations"),
            @ApiResponse(responseCode = "500"),
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Flux<Integration>> getIntegrations() {
        return ResponseEntity.ok().body(integrationService.findAll());
    }

    @Operation(summary = "Instantiate a new integration", description = "The request body must include an unique name and a predefined type. \\['internal'\\]")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = Integration.class)), mediaType = "application/json") }, description = "Successfully created new integration"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters or requested integration already exists."),
            @ApiResponse(responseCode = "404", description = "Integration type not found."),
            @ApiResponse(responseCode = "500"),
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Mono<Integration>> createIntegration(@RequestBody @Valid IntegrationDTO integrationDTO) throws Exception {
        System.out.println(integrationDTO.toString());
        return ResponseEntity.ok().body(integrationService.saveIntegration(integrationDTO.getName(), integrationDTO.getType()));
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
        try{      
            Integration integration = integrationService.findById(integrationId).block();

            if (integration == null) throw new NotFoundException();

            return integration.saveIntegrationData(integrationData);
        } catch (Exception e) {
            return null;
        }
    }




    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleException(Exception ex) {
        CustomResponse response = new CustomResponse(ex.getMessage(), "error", "Internal Server Error");
        return ResponseEntity.internalServerError().body(response);
    }

    @ResponseBody
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<CustomResponse> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex) {
        CustomResponse response = new CustomResponse(ex.getMessage(), "error", "Bad Request");
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleUnfoundRequest(NotFoundException ex) {
        return ((BodyBuilder) ResponseEntity.notFound()).body(ex.getMessage());
    }
}