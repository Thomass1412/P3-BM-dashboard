package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.aau.p3.performancedashboard.model.InternalIntegration;

public interface InternalIntegrationRepository extends ReactiveMongoRepository<InternalIntegration, String>{
}