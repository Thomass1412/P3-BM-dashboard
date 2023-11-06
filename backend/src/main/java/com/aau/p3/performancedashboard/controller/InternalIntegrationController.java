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

import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.service.InternalIntegrationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class InternalIntegrationController {

    @Autowired
    InternalIntegrationService internalIntegrationService;

    @GetMapping("/integration")
    @ResponseStatus(HttpStatus.OK)
    public Flux<InternalIntegration> getAllInternalIntegrations(@RequestParam(required = false) String title) {
        if (title == null) {
            return internalIntegrationService.findAll();
        } else {
            return internalIntegrationService.findByTitleContaining(title);
        }
    }

    @GetMapping("/integration/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<InternalIntegration> getIntegrationById(@PathVariable("id") String id) {
        return internalIntegrationService.findById(id);
    }

    @PostMapping("/integration")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InternalIntegration> createIntegration(@RequestBody InternalIntegration internalIntegration) {
        return internalIntegrationService
                .save(new InternalIntegration(internalIntegration.getId(), internalIntegration.getTitle(),
                        internalIntegration.getData()));
    }

    @PostMapping("/integration/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InternalIntegration> createIntegrationField(@PathVariable("id") String id,
            @RequestBody InternalIntegration internalIntegration) {
        InternalIntegration possibleIntegration = internalIntegrationService.findById(id).block();
        if (possibleIntegration == null) {
            return null;
        }
        possibleIntegration.appendData(internalIntegration.getDatapoint());
        return internalIntegrationService.save(possibleIntegration);
    }
}
