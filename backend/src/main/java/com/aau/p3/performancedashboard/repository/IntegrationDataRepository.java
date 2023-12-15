package com.aau.p3.performancedashboard.repository;

import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;

import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents a repository for IntegrationDataResponse objects.
 * It extends the ReactiveMongoRepository interface and provides methods for querying and manipulating IntegrationDataResponse objects in the database.
 */
@Repository
public interface IntegrationDataRepository extends ReactiveMongoRepository<IntegrationDataResponse, String>{
    
    /**
     * Retrieves all IntegrationDataResponse objects that match the given query and are paginated according to the provided pageable object.
     *
     * @param query    the query to filter the results
     * @param pageable the pageable object specifying the pagination parameters
     * @return a Flux of IntegrationDataResponse objects that match the query and are paginated
     */
    public Flux<IntegrationDataResponse> findAllBy(Query query, Pageable pageable);
}
