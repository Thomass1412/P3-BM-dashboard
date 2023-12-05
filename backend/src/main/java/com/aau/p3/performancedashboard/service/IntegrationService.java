package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import reactor.core.publisher.Mono;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class IntegrationService {
  private static final Logger logger = LogManager.getLogger(IntegrationService.class.getName());

  @Autowired
  IntegrationRepository integrationRepository;

  @Autowired
  InternalIntegrationRepository internalIntegrationRepository;

  // GET all integrations
  public Mono<Page<Integration>> findAllBy(Pageable pageable) {
    return integrationRepository.findAllBy(pageable)
                .collectList()
                .zipWith(integrationRepository.count())
                .map(objects -> new PageImpl<>(objects.getT1(), pageable, objects.getT2()));
  }

  // POST new integration
  public Mono<Integration> saveIntegration(IntegrationDTO dto) throws Exception {
    // If an integration with the name already exists
    if (null != integrationRepository.findByName(dto.getName()).block()) {
      return Mono.error(new IllegalArgumentException("Integration with name '" + dto.getName() + "' already exists."));
    }

    // Check the type, and instantiate corresponding integration class.
    if (dto.getType().equals("internal")) {
      IntegrationDataService integrationDataService = new IntegrationDataService();
      InternalIntegration ie = new InternalIntegration(dto.getName());

      String schema = "{\n" +
        "  \"type\": \"object\",\n" +
        "  \"properties\": {\n" +
        "    \"data\": {\n" +
        "      \"type\": \"object\"\n" +
        "    }\n" +
        "  }\n" +
        "}";

      return integrationDataService.createCollection(ie.getName() + "-data", schema)
        .flatMap(collection -> {
          // Set the collection name after successful creation
          ie.setDataCollection(collection.getNamespace().getCollectionName());
          return internalIntegrationRepository.save(ie);
        })
        .map(Integration.class::cast);
    }

    // For now, return generic error if type cannot be matched
    return Mono.error(new RuntimeException("Something went wrong saving the integration"));
  }

  // Retrieves integration by ID
  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id)
        .switchIfEmpty(Mono.error(new IntegrationNotFoundException("No integration with given ID: " + id)));
  }

  // Retrieves integration by name
  public Mono<Integration> findByName(String name) {
    return integrationRepository.findByName(name);
  }

}