package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.converter.MetricConverter;
import com.aau.p3.performancedashboard.events.IntegrationDataEvent;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.Metric;
import com.aau.p3.performancedashboard.payload.MetricUserCount;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResultResponse;
import com.aau.p3.performancedashboard.repository.MetricRepository;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MetricService implements PropertyChangeListener {

    // Logger
    private static final Logger logger = LogManager.getLogger(MetricService.class);

    // Dependencies
    private final MetricRepository metricRepository;
    private final IntegrationDataService integrationDataService;
    private final IntegrationService integrationService;
    private final ReactiveMongoTemplate mongoTemplate;
    private final MetricConverter metricConverter;

    // Constructor injection
    @Autowired
    public MetricService(MetricRepository metricRepository, IntegrationDataService integrationDataService,
            IntegrationService integrationService,
            ReactiveMongoTemplate mongoTemplate, MetricConverter metricConverter) {
        this.metricRepository = metricRepository;
        this.integrationDataService = integrationDataService;
        this.integrationService = integrationService;
        this.mongoTemplate = mongoTemplate;
        this.metricConverter = metricConverter;
    }

    // Mount on IntegrationDataService when spring has finished loading
    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        logger.debug("Registering as property change listener");
        integrationDataService.addPropertyChangeListener(this);
    }

    private static Map<String, List<String>> metrics = new HashMap<>();

    // Load metrics from database
    @EventListener(ContextRefreshedEvent.class)
    public void loadMetrics() {
        mongoTemplate.findAll(Metric.class)
                .doOnNext(metric -> {
                    for (String integrationId : metric.getDependentIntegrationIds()) {
                        metrics.computeIfAbsent(integrationId, k -> new ArrayList<>()).add(metric.getId());
                    }
                })
                .doOnComplete(() -> {
                    if (metrics.isEmpty()) {
                        logger.info("No metrics found in the database.");
                    } else {
                        logger.info("Loaded metrics from the database." + metrics);
                    }
                })
                .subscribe();
    }

    // Handle integration data events
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.debug("Active metrics: " + metrics);
        if (evt.getNewValue() instanceof IntegrationDataEvent) {
            logger.debug("Received integratwion data event");
            IntegrationDataEvent event = (IntegrationDataEvent) evt.getNewValue();
            logger.debug("Received integration data event for integration with ID: " + event.getIntegrationId());
            logger.debug("Integration data: " + event.getIntegrationDataRequest().getData());
            metricTester(event);
        }
    }

    public Flux<Double> getCountsByUser(String collectionName) {
        MatchOperation matchOperation = new MatchOperation(Criteria.where("Field1").is("Bwa"));

        GroupOperation groupOperation = Aggregation.group("userId").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

        return mongoTemplate.aggregate(aggregation, collectionName, Double.class);
    }

    public Mono<MetricResponse> createMetric(CreateMetricRequest createMetricRequest) {
        logger.debug("Creating metric with name: " + createMetricRequest.getName());

        Metric metric = new Metric();
        metric.setName(createMetricRequest.getName());
        metric.setDependentIntegrationIds(createMetricRequest.getDependentIntegrationIds());

        // 1. Ensure that an integration with the same name does not already exist
        // 2. Ensure that all dependent integrations exist
        // 3. Create the metric
        return metricRepository.existsByName(createMetricRequest.getName())
                .flatMap(metricExists -> {
                    if (metricExists) {
                        // Metric with the given name already exists, raise an exception
                        return Mono.error(new IllegalArgumentException(
                                "Metric with name '" + createMetricRequest.getName() + "' already exists."));
                    } else {
                        // Metric with the given name does not exist, proceed to check dependent
                        // integrations
                        List<String> notFoundIds = new ArrayList<>();

                        return Flux.fromIterable(createMetricRequest.getDependentIntegrationIds())
                                .flatMap(id -> integrationService.findById(id)
                                        .switchIfEmpty(Mono.fromRunnable(() -> notFoundIds.add(id))))
                                .then(Mono.defer(() -> {
                                    // If any dependent integration is not found, raise an exception
                                    if (!notFoundIds.isEmpty()) {
                                        return Mono.error(new IntegrationNotFoundException(notFoundIds));
                                    } else {
                                        // All dependent integrations are found, continue with the next step
                                        return Mono.just(true);
                                    }
                                }));
                    }
                })
                .flatMap(isValid -> {
                    // isValid is true only if metric does not exist and all dependent integrations
                    // are found
                    if (isValid) {
                        // Insert the metric into MongoDB
                        return mongoTemplate.insert(metric)
                                .doOnNext(createdMetric -> {
                                    for (String integrationId : createdMetric.getDependentIntegrationIds()) {
                                        metrics.computeIfAbsent(integrationId, k -> new ArrayList<>())
                                                .add(createdMetric.getId());
                                    }
                                }).map(metricConverter::convertToResponse);
                    } else {
                        return Mono.empty(); // Or return an appropriate response or error
                    }
                });
    }

    public Mono<MessageResponse> deleteMetric(String metricId) {
        logger.debug("Deleting metric with ID: " + metricId);

        // 1. Ensure that the metric exists
        // 2. Delete the metric
        return metricRepository.existsById(metricId)
                .flatMap(metricExists -> {
                    if (metricExists) {
                        // Metric exists, proceed to delete it
                        return metricRepository.deleteById(metricId)
                                .then(Mono.defer(() -> {
                                    metrics.remove(metricId);
                                    return Mono.just(new MessageResponse("Metric deleted successfully!"));
                                }));
                    } else {
                        // Metric does not exist, raise an exception
                        return Mono.error(
                                new IllegalArgumentException("Metric with ID '" + metricId + "' does not exist."));
                    }
                });
    }

    private void metricTester(IntegrationDataEvent event) {
        // Extract event
        String integrationId = event.getIntegrationId();
        Integration integration = integrationService.findById(integrationId).block();

        String collectionName = integration.getDataCollection();

        CreateIntegrationDataRequest integrationDataRequest = event.getIntegrationDataRequest();

        // Set dates
        Date endDate = new Date(); // get the latest.
        Date startDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            startDate = dateFormat.parse("2023-09-05T12:08:34.000+00:00");
        } catch (ParseException e) {
            // Handle the exception
            startDate = null; // or provide a default value
        }

        // Create initial MetricResultResponse with empty counts
        MetricResultResponse initialMetricResultResponse = new MetricResultResponse();
        initialMetricResultResponse.setStartDate(startDate);
        initialMetricResultResponse.setEndDate(endDate);
        initialMetricResultResponse.setMetricUserCounts(new ArrayList<>());

        // Perform aggregations
        Mono<MetricResultResponse> resultMono = Mono.just(initialMetricResultResponse)
                .flatMap(response -> metricCountDocumentsByCriteria(response, collectionName, OPERATOR.PLUS))
                .flatMap(response -> metricCountDocumentsByCriteria(response, collectionName, OPERATOR.MINUS))
                .flatMap(response -> metricCountDocumentsByCriteria(response, collectionName, OPERATOR.PLUS));

        // Subscribe to the final result and log
        resultMono.subscribe(finalResponse -> {
            logger.debug("Final Metric result response: " + finalResponse);
            finalResponse.getMetricUserCounts().forEach(metricUserCount -> {
                logger.debug("Metric user count: " + metricUserCount);
            });
        });
    }

    private enum OPERATOR {
        PLUS, MINUS, MULTIPLY, DIVIDE
    }

    public Mono<MetricResultResponse> metricCountDocumentsByCriteria(MetricResultResponse metricResultResponse,
            String collectionName, OPERATOR operator) {

        logger.debug("Metric result response: " + metricResultResponse);
        logger.debug("Collection name: " + collectionName);
        logger.debug("Operator: " + operator);
        logger.debug("Metric user counts: " + metricResultResponse.getMetricUserCounts());

        Criteria dateCriteria = Criteria.where("timestamp").gte(metricResultResponse.getStartDate())
                .lte(metricResultResponse.getEndDate());
        MatchOperation matchOperation = Aggregation.match(dateCriteria);

        GroupOperation groupOperation = Aggregation.group("userId").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

        return mongoTemplate.aggregate(aggregation, collectionName, Map.class)
                .collectList()
                .flatMap(aggregatedResults -> {
                    // Map for storing updated counts
                    Map<String, MetricUserCount> updatedCounts = new HashMap<>();

                    // Initialize with existing counts
                    for (MetricUserCount count : metricResultResponse.getMetricUserCounts()) {
                        updatedCounts.put(count.getUserId(), new MetricUserCount(count.getUserId(), count.getCount()));
                    }

                    // Update counts based on aggregation results
                    for (Map<?, ?> result : aggregatedResults) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> resultMap = (Map<String, Object>) result;

                        String userId = (String) resultMap.get("_id");
                        int newCount = ((Number) resultMap.get("count")).intValue();

                        MetricUserCount currentCount = updatedCounts.getOrDefault(userId,
                                new MetricUserCount(userId, 0));
                        int updatedValue = applyOperator(currentCount.getCount(), newCount, operator);
                        currentCount.setCount(updatedValue);
                        updatedCounts.put(userId, currentCount);
                    }

                    metricResultResponse.setMetricUserCounts(new ArrayList<>(updatedCounts.values()));
                    return Mono.just(metricResultResponse);
                });
    }

    /**
     * Applies the specified operator to the current value and the new value.
     * 
     * @param currentValue the current value
     * @param newValue     the new value
     * @param operator     the operator to apply
     * @return the result of applying the operator to the current value and the new
     *         value
     */
    private int applyOperator(int currentValue, int newValue, OPERATOR operator) {
        switch (operator) {
            case PLUS:
                return currentValue + newValue;
            case MINUS:
                return currentValue - newValue;
            case MULTIPLY:
                return currentValue * newValue;
            case DIVIDE:
                if (newValue != 0) {
                    return currentValue / newValue;
                } else {
                    // division by zero
                    return currentValue;
                }
            default:
                return currentValue;
        }
    }
}
