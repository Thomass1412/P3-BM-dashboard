package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class IntegrationService {
  protected static final Logger logger = LogManager.getLogger(IntegrationService.class.getName());

  @Autowired
  IntegrationRepository integrationRepository;

  @Autowired
  InternalIntegrationRepository internalIntegrationRepository;

  // GET all integrations
  public Flux<Integration> findAll() {
    return integrationRepository.findAll();
  }

  // POST new integration
  public Mono<Integration> saveIntegration(String name, String type) throws Exception {

    // If an integration with the name already exists
    if (null != integrationRepository.findByName(name).block()) {
      return Mono.error(new IllegalArgumentException("Integration with name '" + name + "' already exists."));
    }

    // Check the type, and instantiate corresponding integration class.
    if (type.equals("internal")) {
      IntegrationDataService integrationDataService = new IntegrationDataService();
      InternalIntegration ie = new InternalIntegration(name);
      ie.setDataCollection(integrationDataService.createCollection(ie.getName() + "-data"));
      Mono<Integration> res = internalIntegrationRepository.save(ie).map(Integration.class::cast);
      return res;
    }

    // For know, return generic error if type cannot be matched
    return Mono.error(new RuntimeException("Something went wrong saving the integration"));
  }

  // Retrieves integration by ID
  public Mono<Integration> findById(String id) {

    Mono<Integration> res = integrationRepository.findById(id);
    if (res == null) {
      return Mono.error(new NotFoundException());
    }
    return res;
  }

  // Retrieves integration by name
  public Mono<Integration> findByName(String name) {
    return integrationRepository.findByName(name);
  }

}