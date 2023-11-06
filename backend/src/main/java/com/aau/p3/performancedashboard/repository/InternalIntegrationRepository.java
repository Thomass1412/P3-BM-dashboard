package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.InternalIntegration;

import reactor.core.publisher.Flux;

@Repository
public interface InternalIntegrationRepository extends ReactiveMongoRepository<InternalIntegration, String> {
  Flux<InternalIntegration> findByTitleContaining(String title);
}
