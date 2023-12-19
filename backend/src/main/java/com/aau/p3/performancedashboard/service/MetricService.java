package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.converter.MetricConverter;
import com.aau.p3.performancedashboard.events.IntegrationDataEvent;
import com.aau.p3.performancedashboard.exceptions.IntegrationNotFoundException;
import com.aau.p3.performancedashboard.exceptions.MetricNotFoundException;
import com.aau.p3.performancedashboard.metricBuilder.MetricOperationEnum;
import com.aau.p3.performancedashboard.model.Metric;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.MetricUserCount;
import com.aau.p3.performancedashboard.payload.request.CreateMetricRequest;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResultResponse;
import com.aau.p3.performancedashboard.payload.response.UserResponse;
import com.aau.p3.performancedashboard.repository.MetricRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MetricService implements PropertyChangeListener {

    // Logger
    private static final Logger logger = LogManager.getLogger(MetricService.class);

    // Dependencies
    private final MetricRepository metricRepository;
    private final UserRepository userRepository;
    private final IntegrationDataService integrationDataService;
    private final IntegrationService integrationService;
    private final ReactiveMongoTemplate mongoTemplate;
    private final MetricConverter metricConverter;

    // Constructor injection
    @Autowired
    public MetricService(MetricRepository metricRepository, UserRepository userRepository,
            IntegrationDataService integrationDataService,
            IntegrationService integrationService,
            ReactiveMongoTemplate mongoTemplate, MetricConverter metricConverter) {
        this.metricRepository = metricRepository;
        this.userRepository = userRepository;
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
            // metricTester(event);
        }
    }

    /**
     * Calculates the metric based on the provided parameters.
     *
     * @param metricId    The ID of the metric to calculate.
     * @param startDate   The start date for the calculation.
     * @param endDate     The end date for the calculation.
     * @param resultLimit The maximum number of results to return.
     * @param sortOrder   The sort order for the results.
     * @return A Mono containing the MetricResultResponse.
     * @throws MetricNotFoundException if the metric with the given ID is not found.
     */
    public Mono<MetricResultResponse> calculateMetric(String metricId, Date startDate, Date endDate,
            Integer resultLimit, String sortOrder) {
        logger.debug(
                "Calculating metric for metricId: " + metricId + ", startDate: " + startDate + ", endDate: " + endDate);

        return metricRepository.findById(metricId)
                .switchIfEmpty(Mono.error(new MetricNotFoundException("Metric not found with ID: " + metricId)))
                .flatMap(metric -> {
                    logger.debug("Found metric with ID: " + metricId);

                    AtomicReference<MetricOperationEnum> currentOperator = new AtomicReference<>(
                            MetricOperationEnum.ADD);

                    return Flux.fromIterable(metric.getMetricOperations())
                            .flatMap(metricOperation -> {
                                logger.debug("Processing metric operation: " + metricOperation);
                                if (isBigFourOperation(metricOperation.getOperation())) {
                                    logger.debug("Setting current operator to: " + metricOperation.getOperation());
                                    currentOperator.set(metricOperation.getOperation());
                                    return Flux.empty(); // No counts to return in this case
                                } else if (MetricOperationEnum.COUNT.equals(metricOperation.getOperation())) {
                                    logger.debug("Count operation found. Proceeding to count.");
                                    return integrationService.findById(metricOperation.getTargetIntegration())
                                            .flatMap(integration -> metricCount(integration.getDataCollection(),
                                                    startDate, endDate, metricOperation.getCriteria(), resultLimit,
                                                    Optional.of(sortOrder)))
                                            .map(counts -> new AbstractMap.SimpleEntry<>(currentOperator.get(),
                                                    counts));
                                } else {
                                    logger.error("Unrecognized operation: " + metricOperation.getOperation());
                                    return Flux.error(new IllegalArgumentException(
                                            "Unrecognized operation: " + metricOperation.getOperation()));
                                }
                            })
                            .collectList()
                            .map(entries -> {
                                logger.debug("Combining counts...");
                                logger.debug("Entries: " + entries);
                                List<MetricUserCount> combinedCounts = new ArrayList<>();
                                for (AbstractMap.SimpleEntry<MetricOperationEnum, List<MetricUserCount>> entry : entries) {
                                    logger.debug("Combining counts for operator: " + entry.getKey());
                                    combinedCounts = combineCounts(combinedCounts, entry.getValue(), entry.getKey());
                                }
                                return combinedCounts;
                            })
                            .map(combinedCounts -> {
                                logger.debug("Creating MetricResultResponse...");
                                MetricResultResponse response = new MetricResultResponse();
                                response.setMetricId(metricId);
                                response.setMetricName(metric.getName());
                                response.setStartDate(startDate);
                                response.setEndDate(endDate);
                                response.setMetricUserCounts(combinedCounts);
                                logger.debug("Created MetricResultResponse: " + response);

                                // Sort the counts
                                if ("DESC".equalsIgnoreCase(sortOrder)) {
                                    logger.debug("Sorting counts in descending order");
                                    response.getMetricUserCounts()
                                            .sort((o1, o2) -> Integer.compare(o2.getCount(), o1.getCount()));
                                } else {
                                    logger.debug("Sorting counts in ascending order");
                                    response.getMetricUserCounts()
                                            .sort((o1, o2) -> Integer.compare(o1.getCount(), o2.getCount()));
                                }

                                return response;
                            });
                })
                .doOnSuccess(response -> logger.info("Metric calculation completed for metricId: " + metricId))
                .doOnError(e -> logger
                        .error("Failed to calculate metric for metricId: " + metricId + ", Error: " + e.getMessage()));
    }

    /**
     * Combines the counts from two lists of MetricUserCount objects using the
     * specified operator.
     * The counts are accumulated in a map, where the key is the user ID and the
     * value is the MetricUserCount object.
     * If a user ID already exists in the map, the counts are combined using the
     * operator.
     * If a user ID does not exist in the map, a new MetricUserCount object is
     * created with a count of 0 and added to the map.
     * The final combined counts are returned as a list of MetricUserCount objects.
     *
     * @param accumulatedCounts The list of MetricUserCount objects with accumulated
     *                          counts.
     * @param newCounts         The list of MetricUserCount objects with new counts
     *                          to be combined.
     * @param operator          The operator to be used for combining the counts.
     * @return The combined counts as a list of MetricUserCount objects.
     */
    private List<MetricUserCount> combineCounts(List<MetricUserCount> accumulatedCounts,
            List<MetricUserCount> newCounts, MetricOperationEnum operator) {
        Map<String, MetricUserCount> combinedCountMap = new HashMap<>();

        // Add all the counts from accumulatedCounts to the map
        for (MetricUserCount count : accumulatedCounts) {
            combinedCountMap.put(count.getUserId(), count);
        }

        // Combine the newCounts with the existing ones in the map
        for (MetricUserCount newCount : newCounts) {
            MetricUserCount existingCount = combinedCountMap.getOrDefault(newCount.getUserId(),
                    new MetricUserCount(newCount.getUserId(), newCount.getDisplayName(), 0));

            int updatedValue = applyOperator(existingCount.getCount(), newCount.getCount(), operator);
            existingCount.setCount(updatedValue);
            combinedCountMap.put(newCount.getUserId(), existingCount);
        }

        return new ArrayList<>(combinedCountMap.values());
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
    private int applyOperator(int currentValue, int newValue, MetricOperationEnum operator) {
        switch (operator) {
            case ADD:
                return currentValue + newValue;
            case SUBTRACT:
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
                logger.error("Invalid operator: " + operator);
                return currentValue;
        }
    }

    /**
     * Checks if the given operation is one of the big four operations: ADD,
     * SUBTRACT, MULTIPLY, or DIVIDE.
     *
     * @param operation the operation to check
     * @return true if the operation is one of the big four operations, false
     *         otherwise
     */
    private boolean isBigFourOperation(MetricOperationEnum operation) {
        return operation == MetricOperationEnum.ADD ||
                operation == MetricOperationEnum.SUBTRACT ||
                operation == MetricOperationEnum.MULTIPLY ||
                operation == MetricOperationEnum.DIVIDE;
    }

    public Mono<List<MetricUserCount>> metricCount(String collectionName, Date startDate, Date endDate,
            Map<String, String> customCriteria, Integer queryLimit, Optional<String> sortOrder) {
        // Create criteria for the date
        Criteria dateCriteria = Criteria.where("timestamp").gte(startDate).lte(endDate);
        // Create a list for all criteria
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(dateCriteria);

        // Add custom criteria to the list
        for (Map.Entry<String, String> entry : customCriteria.entrySet()) {
            criteriaList.add(Criteria.where(entry.getKey()).is(entry.getValue()));
        }

        // Combine all criteria using 'andOperator'
        Criteria dynamicCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        logger.debug("Query criteria: " + dynamicCriteria.getCriteriaObject().toJson());

        MatchOperation matchOperation = Aggregation.match(dynamicCriteria);
        GroupOperation groupOperation = Aggregation.group("userId").count().as("count");
        List<AggregationOperation> aggregationOperations = new ArrayList<>(
                Arrays.asList(matchOperation, groupOperation));

        // Add sorting operation based on sortOrder
        if (sortOrder.isPresent()) {
            Sort.Direction sortDirection = "DESC".equalsIgnoreCase(sortOrder.get()) ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            SortOperation sortOperation = Aggregation.sort(sortDirection, "count");
            aggregationOperations.add(sortOperation);
        }

        // Add a limit operation if queryLimit is greater than 0
        if (queryLimit > 0) {
            LimitOperation limitOperation = Aggregation.limit(queryLimit);
            aggregationOperations.add(limitOperation);
        }

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);

        return mongoTemplate.aggregate(aggregation, collectionName, Map.class)
                .log()
                .collectList()
                .flatMap(aggregatedResults -> {
                    logger.debug("Aggregated results: " + aggregatedResults);
                    // Map to hold the counts
                    Map<String, MetricUserCount> countsMap = new HashMap<>();

                    // Fetch display names for the user IDs
                    Set<String> userIds = aggregatedResults.stream()
                            .map(result -> (String) result.get("_id"))
                            .collect(Collectors.toSet());

                    return Flux.fromIterable(userIds)
                            .flatMap(userRepository::findById)
                            .collectMap(User::getId, User::getDisplayName)
                            .map(displayNames -> {
                                // Process aggregated results
                                for (Map<?, ?> result : aggregatedResults) {
                                    String userId = (String) result.get("_id");
                                    int count = ((Number) result.get("count")).intValue();
                                    countsMap.put(userId, new MetricUserCount(userId,
                                            displayNames.getOrDefault(userId, "Unknown"), count));
                                }
                                return new ArrayList<>(countsMap.values());
                            });
                });
    }

    public Mono<Page<MetricResponse>> findAllBy(Pageable pageable) {
        return metricRepository.findAllBy(pageable)
                .flatMap(metric -> Mono.just(metricConverter.convertToResponse(metric)))
                .collectList()
                .zipWith(metricRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
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

    public Mono<MetricResponse> createMetric(CreateMetricRequest createMetricRequest) {
        logger.debug("Creating metric with name: " + createMetricRequest.getName());

        // Use the converter to create the Metric entity
        Metric metric = metricConverter.convertToEntity(createMetricRequest);

        logger.debug("HERE! Metric: " + metric);
        // Check if a metric with the given name already exists
        return metricRepository.existsByName(createMetricRequest.getName())
                .flatMap(metricExists -> {
                    if (metricExists) {
                        logger.error("Metric with name '" + createMetricRequest.getName() + "' already exists.");
                        return Mono.error(new IllegalArgumentException(
                                "Metric with name '" + createMetricRequest.getName() + "' already exists."));
                    } else {
                        logger.debug("Metric with name '" + createMetricRequest.getName()
                                + "' does not exist. Proceeding with creation.");

                        // List of integration IDs that were not found
                        List<String> notFoundIds = new ArrayList<>();

                        // Check if the dependent integrations exist
                        return Flux.fromIterable(metric.getDependentIntegrationIds())
                                .flatMap(id -> {
                                    logger.debug("Checking existence of integration with ID: " + id);
                                    return integrationService.findById(id)
                                            .doOnNext(integration -> logger.debug("Found integration with ID: " + id))
                                            .switchIfEmpty(Mono.fromRunnable(() -> {
                                                logger.warn("Integration with ID " + id + " not found.");
                                                notFoundIds.add(id);
                                            }));
                                })
                                .collectList()
                                .flatMap(list -> {
                                    if (!notFoundIds.isEmpty()) {
                                        logger.error("Some dependent integrations not found: " + notFoundIds);
                                        return Mono.error(new IntegrationNotFoundException(notFoundIds));
                                    } else {
                                        logger.debug("All dependent integrations found. Proceeding to insert metric.");
                                        return mongoTemplate.insert(metric)
                                                .doOnSuccess(createdMetric -> logger
                                                        .debug("Successfully created metric with ID: "
                                                                + createdMetric.getId()))
                                                .map(metricConverter::convertToResponse);
                                    }
                                });
                    }
                });
    }
}
