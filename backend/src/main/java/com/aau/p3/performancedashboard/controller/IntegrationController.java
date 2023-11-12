package com.aau.p3.performancedashboard.controller;

import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.dto.IntegrationMapper;
import com.aau.p3.performancedashboard.service.IntegrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IntegrationDTO> getIntegrations() {
        return integrationService.findAll();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED) // https://stackoverflow.com/questions/48238250/how-to-use-reactor-core-publisher-monot-or-fluxt-with-inheritance
    public Mono<IntegrationDTO> createIntegration(@RequestBody IntegrationDTO integrationDTO) {
        return integrationService.saveIntegration(integrationDTO).map(IntegrationMapper::toDTO);
    }
    
}
