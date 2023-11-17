package com.aau.p3.performancedashboard.controller;

import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.service.IntegrationService;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.IntegrationData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Integration> getIntegrations() {
        return integrationService.findAll();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED) // https://stackoverflow.com/questions/48238250/how-to-use-reactor-core-publisher-monot-or-fluxt-with-inheritance
    public Mono<Integration> createIntegration(@RequestBody Integration integration) {
        Integration ie = new Integration(integration.getName(), integration.getType());
        return integrationService.saveIntegration(ie);
    }

    @PostMapping("/{integrationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IntegrationData> createIntegrationData(@PathVariable String integrationId,
            @RequestBody IntegrationData integrationData) {
        Integration integration = integrationService.findById(integrationId).block();
        return integration.saveIntegrationData(integrationData);
    }
}
