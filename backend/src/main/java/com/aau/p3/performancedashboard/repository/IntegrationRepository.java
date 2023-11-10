package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.integration.Integration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IntegrationRepository extends ReactiveMongoRepository<Integration, String> {
  Flux<Integration> findAll();
  Flux<Integration> findByName(String name);
  Mono<Integration> findById(String id);
}
