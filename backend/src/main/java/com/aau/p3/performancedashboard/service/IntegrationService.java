package com.aau.p3.performancedashboard.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.converter.IntegrationConverter;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationResponse;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import reactor.core.publisher.Mono;

@Service
public class IntegrationService {
  private static final Logger logger = LogManager.getLogger(IntegrationService.class.getName());

  private final IntegrationRepository integrationRepository;
  private final InternalIntegrationRepository internalIntegrationRepository;
  private final IntegrationDataService integrationDataService;

  /**
   * This constructor is used to inject the {@link IntegrationRepository} and {@link InternalIntegrationRepository} dependencies.
   *
   * @param integrationRepository The repository to interact with the Integration data.
   * @param internalIntegrationRepository The repository to interact with the InternalIntegration data.
   */
  @Autowired
  public IntegrationService(IntegrationRepository integrationRepository, InternalIntegrationRepository internalIntegrationRepository, @Lazy IntegrationDataService integrationDataService) {
      this.integrationRepository = integrationRepository;
      this.internalIntegrationRepository = internalIntegrationRepository;
      this.integrationDataService = integrationDataService;
  }

  // GET all integrations
  public Mono<Page<Integration>> findAllBy(Pageable pageable) {
    return integrationRepository.findAllBy(pageable)
                .collectList()
                .zipWith(integrationRepository.count())
                .map(objects -> new PageImpl<>(objects.getT1(), pageable, objects.getT2()));
  }

  // POST new integration
  public Mono<IntegrationResponse> createIntegration(CreateIntegrationRequest integrationRequest) {
    // If an integration with the name already exists return early with an error.
    if (null != integrationRepository.findByName(integrationRequest.getName()).block()) {
      return Mono.error(new IllegalArgumentException("Integration with name '" + integrationRequest.getName() + "' already exists."));
    }

    // Check the type, and instantiate corresponding integration class.
    if (integrationRequest.getType().equals("internal")) {
      
      // Create a new collection
      String collectionName = null;
      try {
        collectionName = integrationDataService.createCollection(integrationRequest).block();
      } catch (Exception e) {
        logger.error("Error creating collection: " + e.getMessage());
        return Mono.error(new RuntimeException("Something went wrong creating the collection", e));
      }

      // Insert the collection name into the Integration
      InternalIntegration internalIntegration = new InternalIntegration(integrationRequest.getName(), collectionName);

      // Save the integration
      return internalIntegrationRepository.save(internalIntegration)
        .map(integration -> IntegrationConverter.convertAnyIntegrationToIntegrationResponse(integration))
        .switchIfEmpty(Mono.error(new RuntimeException("Something went wrong saving the integration")));
    } else {
      return Mono.error(new IllegalArgumentException("Integration type '" + integrationRequest.getType() + "' is not supported. Currently only 'internal' is supported."));
    }
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