package com.aau.p3.performancedashboard.repository;

import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;

import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationDataRepository extends ReactiveMongoRepository<IntegrationDataResponse, String>{
    public Flux<IntegrationDataResponse> findAllBy(Query query, Pageable pageable);
}
