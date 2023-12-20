package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.Integration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a repository for managing {@link Integration} instances.
 * This interface extends the {@link ReactiveMongoRepository} interface from Spring Data MongoDB to provide CRUD operations for {@link Integration} instances.
 * This interface uses the {@link Repository} annotation from Spring to indicate that it is a repository.
 */
@Repository
public interface IntegrationRepository extends ReactiveMongoRepository<Integration, String> {

    /**
     * Retrieves all {@link Integration} instances with pagination.
     * This method uses the derived query feature of Spring Data MongoDB to automatically generate a query to find all {@link Integration} instances with the given pagination.
     *
     * @param pageable The pagination information.
     * @return A {@link Flux} containing the {@link Integration} instances for the given page, or {@link Flux#empty()} if no {@link Integration} instances exist for the given page.
     */
    public Flux<Integration> findAllBy(Pageable pageable);

    /**
     * Checks if an integration exists by its name.
     *
     * @param name the name of the integration
     * @return true if an integration with the given name exists, false otherwise
     */
    public Mono<Boolean> existsByName(String integrationName);

    /**
     * Checks if an integration exists by its id.
     *
     * @param id the id of the integration
     * @return true if an integration with the given id exists, false otherwise
     */
    public Mono<Boolean> existsById(String itegrationId);

    /**
     * Finds an {@link Integration} by its name.
     * This method uses the derived query feature of Spring Data MongoDB to automatically generate a query to find an {@link Integration} by its name.
     *
     * @param name The name of the {@link Integration} to find.
     * @return A {@link Mono} containing the {@link Integration} with the given name, or {@link Mono#empty()} if no {@link Integration} with the given name exists.
     */
    public Mono<Integration> findByName(String integrationName);
    
}

  