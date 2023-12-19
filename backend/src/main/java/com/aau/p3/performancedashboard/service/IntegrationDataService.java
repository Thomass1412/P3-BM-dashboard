package com.aau.p3.performancedashboard.service;

import org.bson.Document;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;
import com.aau.p3.performancedashboard.converter.IntegrationDataConverter;
import com.aau.p3.performancedashboard.events.IntegrationDataEvent;
import com.aau.p3.performancedashboard.exceptions.NotFoundException;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IntegrationDataService {

	// Logger
	private static final Logger logger = LogManager.getLogger(IntegrationDataService.class);

	// Event management -> To notify MetricService about new integration data
	private final PropertyChangeSupport propertyChangeSupport;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		logger.debug("Adding property change listener");
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	private void notifyListeners(IntegrationDataEvent event) {
		logger.debug("Notifying listeners about integration data event");
		logger.debug(propertyChangeSupport.getPropertyChangeListeners().length + " listeners registered");
        propertyChangeSupport.firePropertyChange("integrationDataEvent", null, event);
    }

	// Dependencies
	private final IntegrationService integrationService;
	private final ReactiveMongoOperations mongoOperations;
	private final ReactiveMongoTemplate mongoTemplate;
	private final MongoDatabase mongoDatabase;
	private final UserService userService;

	// Constructor injection
	@Autowired
	public IntegrationDataService(IntegrationService integrationService, ReactiveMongoOperations mongoOperations,
			ReactiveMongoTemplate mongoTemplate, MongoDatabase mongoDatabase, UserService userService) {
		this.integrationService = integrationService;
		this.mongoOperations = mongoOperations;
		this.mongoTemplate = mongoTemplate;
		this.mongoDatabase = mongoDatabase;
		this.userService = userService;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Finds all integration data by integration ID and pageable.
	 *
	 * @param integrationId the ID of the integration
	 * @param pageable      the pageable object for pagination
	 * @return a Mono of Page<IntegrationDataResponse> containing the integration
	 *         data
	 */
	public Mono<Page<IntegrationDataResponse>> findAllBy(String integrationId, Pageable pageable) {
		logger.debug("Finding all data for integration with id: " + integrationId + " and pageable: " + pageable.toString());
		return integrationService.findById(integrationId)
				.flatMap(integration -> {
					if (!integration.getType().equals("internal")) {
						return Mono.error(new IllegalArgumentException("Integration with id '" + integrationId + "' is not internal."));
					} else {
						// Get the name of the data collection
						String dataCollection = integration.getDataCollection();

						// Create a query object with the provided pageable
						Query query = new Query().with(pageable);

						// Find the data and count the total number of elements
						logger.debug("Finding data in collection: " + dataCollection + " with query: " + query.toString());
						Flux<IntegrationDataResponse> data = mongoTemplate.find(query, IntegrationDataResponse.class, dataCollection);
						Mono<Long> count = mongoTemplate.count(new Query(), dataCollection);

						count.doOnNext(
								value -> logger.debug("Found " + value + " elements in collection: " + dataCollection))
								.subscribe();

						// Return a Mono emitting a Page of IntegrationDataResponse objects
						return data
								.collectList()
								.zipWith(count)
								.map(objects -> new PageImpl<>(objects.getT1(), pageable, objects.getT2()));
					}
				});
	}

	/**
	 * Creates a new MongoDB collection with a JSON schema derived from the provided
	 * {@link CreateIntegrationRequest}.
	 * The schema is generated by calling
	 * {@link CreateIntegrationRequest#generateSchema()}.
	 * If the schema generation is successful, a new collection is created with the
	 * generated schema.
	 * If the schema generation fails, an error is logged and a
	 * {@link RuntimeException} is propagated.
	 *
	 * @param integrationRequest the {@link CreateIntegrationRequest} from which to
	 *                           generate the schema and collection name.
	 * @return a {@link Mono<String>} that emits the name of the created collection
	 *         when the operation is successful.
	 *         The Mono completes empty if the operation fails.
	 * @throws RuntimeException if an error occurs while generating the schema.
	 */
	public Mono<String> createCollection(CreateIntegrationRequest integrationRequest) {
		return Mono.fromCallable(integrationRequest::generateSchema)
				.doOnSuccess(s -> logger
						.info("Created schema: " + s.toString() + " for collection: " + integrationRequest.getName()))
				.doOnError(
						e -> logger.error("Error creating schema for collection: " + integrationRequest.getName(), e))
				.onErrorResume(e -> Mono.error(
						new RuntimeException("Error creating schema: " + e.getMessage(), e)))
				.flatMap(schema -> {
					// Create a validation options object
					ValidationOptions validationOptions = new ValidationOptions()
							.validator(schema.toDocument())
							.validationLevel(ValidationLevel.STRICT)
							.validationAction(ValidationAction.ERROR);

					// Create a collection options object
					CreateCollectionOptions collectionOptions = new CreateCollectionOptions()
							.validationOptions(validationOptions);

					// Extract the collection name from the request
					String collectionName = integrationRequest.getName() + "_data";

					// Create the collection with the schema and return the collection name
					return Mono.from(mongoDatabase.createCollection(collectionName, collectionOptions))
							.doOnSuccess(collection -> logger.info("Created collection: " + collectionName))
							.thenReturn(collectionName);
				});
	}

	/**
	 * Saves integration data for a given integration ID.
	 *
	 * @param integrationId          The ID of the integration.
	 * @param integrationDataRequest The request object containing the integration
	 *                               data to be saved.
	 * @return A Mono emitting the IntegrationDataResponse after saving the data.
	 */
	public Mono<IntegrationDataResponse> saveIntegrationData(String integrationId, CreateIntegrationDataRequest integrationDataRequest) {
		logger.debug("Saving integration data for integration with id: " + integrationId + " and request: " + integrationDataRequest.toString());

		// First check the user exists, then process the integration data by using a helper method
		return userService.findById(integrationDataRequest.getUserId())
				.switchIfEmpty(Mono.error(new NotFoundException("User not found.")))
				.flatMap(user -> processIntegration(integrationId, integrationDataRequest, user))
                .doOnSuccess(data -> {
                    // Create an IntegrationDataEvent
                    IntegrationDataEvent event = new IntegrationDataEvent(this, integrationId, integrationDataRequest);

                    // Notify listeners about the event
                    notifyListeners(event);
                });
	}

	/**
	 * Processes the integration data for a given integration ID, integration data request, and user.
	 * 
	 * @param integrationId The ID of the integration.
	 * @param integrationDataRequest The integration data request.
	 * @param user The user performing the integration.
	 * @return A Mono of IntegrationDataResponse representing the processed integration data.
	 * @throws IllegalArgumentException if the integration with the given ID is not internal.
	 */
	public Mono<IntegrationDataResponse> processIntegration(String integrationId, CreateIntegrationDataRequest integrationDataRequest, User user) {
		logger.debug("Processing integration data for integration with id: " + integrationId + " and request: " + integrationDataRequest.toString());

		// First check if the integration is internal, then process the document using a helper method
		// If the integration is not internal, return an error
		return integrationService.findById(integrationId)
				.flatMap(integration -> {
					if (!integration.getType().equals("internal")) {
						return Mono.error(new IllegalArgumentException(	"Integration with id '" + integrationId + "' is not internal."));
					} else {
						return processDocument(integrationId, integrationDataRequest, user, integration);
					}
				});
	}

	/**
	 * Processes a document for integration data.
	 *
	 * @param integrationId The ID of the integration.
	 * @param integrationDataRequest The request object containing integration data.
	 * @param user The user associated with the integration data.
	 * @param integration The integration object.
	 * @return A Mono of IntegrationDataResponse representing the processed document.
	 */
	private Mono<IntegrationDataResponse> processDocument(String integrationId,
			CreateIntegrationDataRequest integrationDataRequest, User user, Integration integration) {
		logger.debug("Processing document for integration with id: " + integrationId + " and request: " + integrationDataRequest.toString());

		// Convert the request to a document and add the integration ID
		Document document = IntegrationDataConverter.convertCreateIntegrationDataRequestToDocument(integrationId, integrationDataRequest, user);
		document.put("integrationId", (String) integrationId);

		// Save the document to the data collection and convert it to an IntegrationDataResponse
		return mongoOperations.getCollection(integration.getDataCollection())
				.flatMap(collection -> Mono.from(collection.insertOne(document)))
				.doOnSuccess(result -> logger.info("Successfully saved: " + document.toJson() + " to collection: " + integration.getDataCollection()))
				.doOnError(error -> logger.error("Error saving: " + document.toJson() + " to collection: " + integration.getDataCollection(), error))
				.then(IntegrationDataConverter.convertDocumentToIntegrationDataResponse(userService, document))
				.onErrorMap(ClassCastException.class, ex -> new RuntimeException(ex.getMessage(), ex))
				.doOnError(error -> logger.error(	"Error converting document to IntegrationDataResponse: " + error.getMessage(), error));
	}
}
