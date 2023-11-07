package com.aau.p3.performancedashboard.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.service.InternalIntegrationService;
import com.aau.p3.performancedashboard.http.response.InternalIntegrationCreationResponse;
import com.aau.p3.performancedashboard.http.Request.InternalIntegrationCreationRequest;
import com.aau.p3.performancedashboard.http.Request.InternalIntegrationInsertDataRequest;

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
    public InternalIntegrationCreationResponse createIntegration(@RequestBody InternalIntegrationCreationRequest request) {
        internalIntegrationService.save(new InternalIntegration(UUID.randomUUID().toString(), request.getId(), request.getTitle(), new HashMap<String, Long>()));
        return new InternalIntegrationCreationResponse(request.getId());
    }

    @PostMapping("/integration/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InternalIntegration> createIntegrationField(@PathVariable("id") String id, @RequestBody InternalIntegrationInsertDataRequest request) {
        return null;
    }
}
