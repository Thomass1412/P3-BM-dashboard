package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.Metric;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MetricRepository extends ReactiveMongoRepository<Metric, String> {
    
    public Mono<Metric> findByName(String metricName);
    
    public Mono<Boolean> existsByName(String metricName);
    
    public Flux<Metric> findAllBy(Pageable pageable);
}
