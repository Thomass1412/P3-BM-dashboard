package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TestService {
    @Autowired
    ReactiveMongoTemplate mongoTemplate;
    public Mono<Page<IntegrationDataResponse>> findAllBy(String integrationId, Pageable pageable){
        Integration integration = mongoTemplate.findById(integrationId, Integration.class).block();
        String dataCollection = integration.getDataCollection();
        Query query = new Query().with(pageable);
        Flux<IntegrationDataResponse> data = mongoTemplate.find(query, IntegrationDataResponse.class, dataCollection);
        Mono<Long> count = mongoTemplate.count(query, dataCollection);
        return data
        .collectList()
        .zipWith(count)
        .map(objects -> new PageImpl<>(objects.getT1(), pageable, objects.getT2()));
    }
}
