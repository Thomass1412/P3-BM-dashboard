package com.aau.p3.performancedashboard.repository;

import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Represents a repository for managing {@link IntegrationDataResponse} instances.
 * Extends {@link ReactiveMongoRepository} to provide CRUD operations for {@link IntegrationDataResponse} instances.
 * Annotated with {@link Repository} to indicate its role as a repository.
 */
@Repository
public interface IntegrationDataRepository extends ReactiveMongoRepository<IntegrationDataResponse, String>{
    
    /**
     * Retrieves all {@link IntegrationDataResponse} instances with pagination.
     * Uses Spring Data MongoDB's derived query feature for automatic query generation.
     *
     * @param query The query conditions.
     * @param pageable The pagination information.
     * @return A {@link Flux} of {@link IntegrationDataResponse} instances for the given page, or {@link Flux#empty()} if none exist.
     */
    public Flux<IntegrationDataResponse> findAllBy(Query query, Pageable pageable);

    /**
     * Checks if an integration data response exists by its id.
     *
     * @param integrationDataResponseId the unique identifier of the integration data response.
     * @return true if an integration data response with the given id exists, false otherwise.
     */
    public Mono<Boolean> existsById(String integrationDataResponseId);
}
