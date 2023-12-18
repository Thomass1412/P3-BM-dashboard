package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.Metric;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a repository for managing {@link Metric} instances.
 * Extends {@link ReactiveMongoRepository} for CRUD operations on {@link Metric} instances.
 * Annotated with {@link Repository} to indicate its role as a repository.
 */
@Repository
public interface MetricRepository extends ReactiveMongoRepository<Metric, String> {

    /**
     * Retrieves all {@link Metric} instances with pagination.
     * Uses Spring Data MongoDB's derived query feature for automatic query generation.
     *
     * @param pageable The pagination information.
     * @return A {@link Flux} of {@link Metric} instances for the given page, or {@link Flux#empty()} if none exist.
     */
    public Flux<Metric> findAllBy(Pageable pageable);

    /**
     * Checks if a metric exists by its name.
     *
     * @param metricName the name of the metric.
     * @return true if a metric with the given name exists, false otherwise.
     */
    public Mono<Boolean> existsByName(String metricName);

    /**
     * Checks if a metric exists by its id.
     *
     * @param metricId the id of the metric.
     * @return true if a metric with the given id exists, false otherwise.
     */
    public Mono<Boolean> existsById(String metricId);
        
    /**
     * Finds a {@link Metric} by its name.
     * Uses derived query feature for query generation.
     *
     * @param metricName The name of the {@link Metric} to find.
     * @return A {@link Mono} with the {@link Metric}, or {@link Mono#empty()} if not found.
     */
    public Mono<Metric> findByName(String metricName);
    
}
