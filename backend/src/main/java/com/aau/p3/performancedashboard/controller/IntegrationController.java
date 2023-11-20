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

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.IntegrationData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name ="Integration", description = "Integration Management APIs")
@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @Operation(
        summary = "Retrieve all instantiated integrations",
        description = "The response object will inherit from a specific integration subclass. Fields may vary.")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = { @Content(array  = @ArraySchema(schema = @Schema(implementation = Integration.class)), mediaType = "application/json") }, description = "Successfully retrieved all integrations"),
      @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }, description = "Integration type not found." ),
      @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public Flux<ResponseEntity<Flux<Integration>>> getIntegrations() {
        return Flux.just(ResponseEntity.ok().body(integrationService.findAll()));
    }

    @Operation(
        summary = "Instantiate a new integration",
        description = "The request body must include an unique name and a predefined type. \\['internal'\\]")
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED) // https://stackoverflow.com/questions/48238250/how-to-use-reactor-core-publisher-monot-or-fluxt-with-inheritance
    public Mono<ResponseEntity<Mono<Integration>>> createIntegration(@RequestBody IntegrationDTO integrationDTO) {
        try {
            return Mono.just(ResponseEntity.ok().body(integrationService.saveIntegration(integrationDTO.getName(), integrationDTO.getType())));
        } catch (Exception ex) {
            System.out.println("An error occurred while saving the integration: "   + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    @PostMapping("/{integrationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IntegrationData> createIntegrationData(@PathVariable String integrationId,
            @RequestBody IntegrationData integrationData) {
        Integration integration = integrationService.findById(integrationId).block();
        return integration.saveIntegrationData(integrationData);
    }


    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleResponseStatusException(ResponseStatusException ex) {
        return Mono.just("Bad Request: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleException(Exception ex){
        System.err.println("An error occurred: " + ex.getMessage());
        return Mono.just("Internal server error");
    }
}
