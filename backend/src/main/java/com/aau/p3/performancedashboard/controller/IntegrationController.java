package com.aau.p3.performancedashboard.controller;

import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.service.IntegrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.aau.p3.performancedashboard.model.Integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


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
        description = "The response object has info")
    @ApiResponses({
      @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Integration.class), mediaType = "application/json") }),
      @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
      @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public Flux<Integration> getIntegrations() {
        return integrationService.findAll();
    }

    @Operation(
        summary = "Instantiate a new integration",
        description = "The request body must include an unique name and a predefined type. \\['internal'\\]")
    @ApiResponses({
      @ApiResponse(responseCode = "201", content = { @Content(schema = @Schema(implementation = Integration.class), mediaType = "application/json") }),
      @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED) // https://stackoverflow.com/questions/48238250/how-to-use-reactor-core-publisher-monot-or-fluxt-with-inheritance
    public Mono<Integration> createIntegration(@RequestBody IntegrationDTO integrationDTO) {
        Integration integration = new Integration(integrationDTO.getName(), integrationDTO.getType());
        return integrationService.saveIntegration(integration);
    }
    
}
