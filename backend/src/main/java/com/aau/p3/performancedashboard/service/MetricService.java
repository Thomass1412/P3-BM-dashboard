package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.converter.MetricConverter;
import com.aau.p3.performancedashboard.events.IntegrationDataEvent;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.model.Metric;
import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public MetricService(MetricRepository metricRepository, IntegrationDataService integrationDataService, IntegrationService integrationService,
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

    private static Map<String, Metric> metrics = new HashMap<>();

    // Load metrics from database
    @EventListener(ContextRefreshedEvent.class)
    public void loadMetrics() {
        mongoTemplate.findAll(Metric.class)
                .doOnNext(metric -> metrics.put(metric.getId(), metric))
                .doOnComplete(() -> {
                    if (metrics.isEmpty()) {
                        logger.info("No metrics found in the database.");
                    }
                })
                .subscribe();
    }

    // Handle integration data events
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof IntegrationDataEvent) {
            logger.debug("Received integration data event");
            IntegrationDataEvent event = (IntegrationDataEvent) evt.getNewValue();
            logger.debug("Received integration data event for integration with ID: " + event.getIntegrationId());
            logger.debug("Integration data: " + event.getIntegrationDataRequest().getData());
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
                        return Mono.error(new IllegalArgumentException("Metric with name '" + createMetricRequest.getName() + "' already exists."));
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
                    // isValid is true only if metric does not exist and all dependent integrations are found
                    if (isValid) {
                        // Insert the metric into MongoDB
                        return mongoTemplate.insert(metric)
                                .doOnNext(createdMetric -> metrics.put(createdMetric.getId(), createdMetric))
                                .map(metricConverter::convertToResponse);
                    } else {
                        return Mono.empty(); // Or return an appropriate response or error
                    }
                });
    }
}