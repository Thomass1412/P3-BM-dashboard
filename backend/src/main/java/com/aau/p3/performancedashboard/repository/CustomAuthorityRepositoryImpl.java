package com.aau.p3.performancedashboard.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import com.aau.p3.performancedashboard.model.Authority;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import reactor.core.publisher.Mono;

@Component
public class CustomAuthorityRepositoryImpl implements CustomAuthorityRepository {

    // Logger
    private static final Logger logger = LogManager.getLogger(CustomAuthorityRepositoryImpl.class);
    
    // Dependencies
    private final ReactiveMongoTemplate mongoTemplate;

    // Constructor injection
    @Autowired
    public CustomAuthorityRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Authority> findById(String id) {
        return mongoTemplate.findById(id, Authority.class);
    }

    @Override
    public Mono<Authority> findByName(String name) {
        return mongoTemplate.findOne(
                Query.query(Criteria.where("name").is(name.toUpperCase())), Authority.class);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        Mono<Boolean> exists = mongoTemplate.exists(
                Query.query(Criteria.where("name").is(name)), Authority.class);
        exists.subscribe(e -> logger.debug("Exists by name: {}, result: {}", name, e));
        return exists;
    }
}
