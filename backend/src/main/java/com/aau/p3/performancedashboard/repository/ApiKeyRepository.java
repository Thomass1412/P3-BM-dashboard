package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.ApiKey;

import reactor.core.publisher.Flux;

@Repository
public interface ApiKeyRepository extends ReactiveMongoRepository<ApiKey, String> {
    /**
     * Retrieves all integrations based on the specified pageable parameters.
     *
     * @param pageable the pageable parameters for pagination and sorting
     * @return a Flux of Integration objects
     */
    public Flux<ApiKey> findAllBy(Pageable pageable);
}