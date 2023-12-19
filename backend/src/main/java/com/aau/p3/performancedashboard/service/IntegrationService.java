package com.aau.p3.performancedashboard.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
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
import com.aau.p3.performancedashboard.integrationSchema.MongoSchemaRetriever;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;

@Service
public class IntegrationService {

  // Logger
  private static final Logger logger = LogManager.getLogger(IntegrationService.class);

  // Dependencies
  private final IntegrationRepository integrationRepository;
  private final InternalIntegrationRepository internalIntegrationRepository;
  private final IntegrationDataService integrationDataService;
  private final MongoDatabase mongoDatabase;

  // Constructor injection
  @Autowired
  public IntegrationService(IntegrationRepository integrationRepository,
      InternalIntegrationRepository internalIntegrationRepository, @Lazy IntegrationDataService integrationDataService,
      MongoDatabase mongoDatabase) {
    this.integrationRepository = integrationRepository;
    this.internalIntegrationRepository = internalIntegrationRepository;
    this.integrationDataService = integrationDataService;
    this.mongoDatabase = mongoDatabase;
  }

  /**
   * Retrieves all integrations with pagination.
   *
   * @param pageable the pagination information
   * @return a Mono emitting a Page of Integration objects
   */
  public Mono<Page<Integration>> findAllBy(Pageable pageable) {
    return integrationRepository.findAllBy(pageable)
        .collectList()
        .zipWith(integrationRepository.count())
        .flatMap(objects -> {
          List<Integration> integrations = objects.getT1();
          long totalElements = objects.getT2();
          if (integrations.isEmpty()) {
            logger.error("No integrations found.");
            return Mono.error(new IntegrationNotFoundException("No integrations found."));
          } else {
            logger.debug("Found " + integrations.size() + " integrations.");
            return Mono.just(new PageImpl<>(integrations, pageable, totalElements));
          }
        });
  }

  /**
   * Creates a new integration based on the provided integration request.
   * 
   * @param integrationRequest The request object containing the integration details.
   * @return A Mono emitting the integration response.
   * @throws IllegalArgumentException if an integration with the same name already exists.
   */
  public Mono<IntegrationResponse> createIntegration(CreateIntegrationRequest integrationRequest) {
    // Check if integration with given name already exists, or else create it
    return integrationRepository.findByName(integrationRequest.getName())
        .hasElement()
        .flatMap(exists -> {
          if (exists) {
            return Mono.error(new IllegalArgumentException("Integration with name '" + integrationRequest.getName() + "' already exists."));
          } else {
            return createInternalIntegration(integrationRequest);
          }
        });
  }

  /**
   * Creates an internal integration based on the provided integration request.
   * 
   * @param integrationRequest the request object containing the details of the integration to be created
   * @return a Mono that emits the integration response if the integration is created successfully,
   *         otherwise emits an error with an IllegalArgumentException
   */
  protected Mono<IntegrationResponse> createInternalIntegration(CreateIntegrationRequest integrationRequest) {
    // Return a mono which emits the integration response if the integration is created successfully
    // Otherwise emit an error with an IllegalArgumentException
    return Mono.justOrEmpty(integrationRequest)
    .flatMap(request -> {
        if (!request.getType().equals("internal")) {
            return Mono.error(new IllegalArgumentException("Integration type '" + request.getType()
                + "' is not supported. Currently only 'internal' is supported."));
        }

        return integrationDataService.createCollection(request)
            .flatMap(collectionName -> {
                InternalIntegration internalIntegration = new InternalIntegration(request.getName(),
                    collectionName);
                return internalIntegrationRepository.save(internalIntegration);
            })
            .map(IntegrationConverter::convertAnyIntegrationToIntegrationResponse)
            .doOnError(e -> logger.error("Error creating collection: " + e.getMessage()));
    });
  }

  /**
   * Finds an integration by its ID.
   *
   * @param id the ID of the integration to find
   * @return a Mono emitting the found integration, or an error if no integration
   *         is found
   */
  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id)
        .switchIfEmpty(Mono.error(new IntegrationNotFoundException("No integration with given ID: " + id)));
  }

  /**
   * Finds an integration by its name.
   *
   * @param name the name of the integration to find
   * @return a Mono emitting the integration if found, otherwise an empty Mono
   */
  public Mono<Integration> findByName(String name) {
    return integrationRepository.findByName(name);
  }

  /**
   * Retrieves the schema for a given integration ID.
   *
   * @param integrationId The ID of the integration.
   * @return A Mono emitting the schema document.
   */
  public Mono<Document> getSchemaBy(String integrationId) {
    MongoSchemaRetriever retriever = new MongoSchemaRetriever();
    return integrationRepository.findById(integrationId)
        .doOnSuccess(s -> logger.info("Retrieved schema for integration: " + s.getName()))
        .switchIfEmpty(Mono.error(new RuntimeException("Cannot find integration with ID: " + integrationId)))
        .flatMap(integration -> retriever.retrieveSchema(integration.getDataCollection(), mongoDatabase))
        .cast(Document.class) // Cast the result to Mono<Document>
        .map(document -> {
          logger.info("Retrieved schema for integration: " + integrationId);
          return document;
        });
  }
}