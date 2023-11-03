// Invoke-WebRequest -ContentType "application/json" -Uri http://localhost:8080/api/v1/integration/ POST -Body @{id='id1';title='integration title here'}

package com.aau.p3.performancedashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.service.IntegrationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class IntegrationController {
    
    @Autowired
    IntegrationService integrationService;

    @GetMapping("/integration")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Integration> getAllIntegrations(@RequestParam(required = false) String title) {
        if(title == null) {
            return integrationService.findAll();
        } else {
            return integrationService.findByTitleContaining(title);
        }
    }

    @GetMapping("/integration/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Integration> getIntegrationById(@PathVariable("id") String id) {
        return integrationService.findById(id);
    }

    @PostMapping("/integration")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Integration> createIntegration(@RequestBody Integration integration) {
        return integrationService.save(new Integration(integration.getId(), integration.getTitle()));
    }
}
