package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.Integration;

import reactor.core.publisher.Flux;

@Repository
public interface IntegrationRepository extends ReactiveMongoRepository<Integration, String> {
  Flux<Integration> findByTitleContaining(String title);
}
