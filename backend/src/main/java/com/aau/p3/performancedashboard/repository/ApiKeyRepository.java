package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.ApiKey;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a repository for managing {@link ApiKey} instances.
 * Extends {@link ReactiveMongoRepository} to provide CRUD operations for {@link ApiKey} instances.
 * Annotated with {@link Repository} to indicate its role as a repository.
 */
@Repository
public interface ApiKeyRepository extends ReactiveMongoRepository<ApiKey, String> {

    /**
     * Retrieves all {@link ApiKey} instances with pagination.
     * Uses Spring Data MongoDB's derived query feature for automatic query generation.
     *
     * @param pageable The pagination information.
     * @return A {@link Flux} of {@link ApiKey} instances for the given page, or {@link Flux#empty()} if none exist.
     */
    public Flux<ApiKey> findAllBy(Pageable pageable);

    /**
     * Checks if an API key exists by its name.
     *
     * @param apiKeyName the name of the API key.
     * @return true if an API key with the given name exists, false otherwise.
     */
    public Mono<Boolean> existsByName(String apiKeyName);

    /**
     * Checks if an API key exists by its id.
     *
     * @param apiKeyId the unique identifier of the API key.
     * @return true if an API key with the given id exists, false otherwise.
     */
    public Mono<Boolean> existsById(String apiKeyId);

    /**
     * Finds an {@link ApiKey} by its name.
     * Uses derived query feature for query generation.
     *
     * @param apiKeyName The name of the {@link ApiKey} to find.
     * @return A {@link Mono} with the {@link ApiKey}, or {@link Mono#empty()} if not found.
     */
    public Mono<ApiKey> findByName(String apiKeyName);
}