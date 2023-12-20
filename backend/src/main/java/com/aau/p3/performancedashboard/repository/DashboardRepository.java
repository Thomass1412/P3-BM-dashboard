package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.Dashboard;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a repository for managing {@link Dashboard} instances.
 * Extends {@link ReactiveMongoRepository} to provide CRUD operations for {@link Dashboard} instances.
 * Annotated with {@link Repository} to indicate its role as a repository.
 */
@Repository
public interface DashboardRepository extends ReactiveMongoRepository<Dashboard, String> {

    /**
     * Retrieves all {@link Dashboard} instances with pagination.
     * Uses Spring Data MongoDB's derived query feature for automatic query generation.
     *
     * @param pageable The pagination information.
     * @return A {@link Flux} of {@link Dashboard} instances for the given page, or {@link Flux#empty()} if none exist.
     */
    public Flux<Dashboard> findAllBy(Pageable pageable);

    /**
     * Checks if a dashboard exists by its name.
     *
     * @param dashboardName the name of the dashboard.
     * @return true if a dashboard with the given name exists, false otherwise.
     */
    public Mono<Boolean> existsByName(String dashboardName);

    /**
     * Checks if a dashboard exists by its id.
     *
     * @param dashboardId the unique identifier of the dashboard.
     * @return true if a dashboard with the given id exists, false otherwise.
     */
    public Mono<Boolean> existsById(String dashboardId);

    /**
     * Finds a {@link Dashboard} by its name.
     * Uses derived query feature for query generation.
     *
     * @param dashboardName The name of the {@link Dashboard} to find.
     * @return A {@link Mono} with the {@link Dashboard}, or {@link Mono#empty()} if not found.
     */
    public Mono<Dashboard> findByName(String dashboardName);
    
}
