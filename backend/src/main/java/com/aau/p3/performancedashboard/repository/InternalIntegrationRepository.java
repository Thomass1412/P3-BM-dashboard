package com.aau.p3.performancedashboard.repository;

import com.aau.p3.performancedashboard.model.InternalIntegration;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface InternalIntegrationRepository extends ReactiveMongoRepository<InternalIntegration, String> {
}
