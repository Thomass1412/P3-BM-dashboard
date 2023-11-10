package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.integration.Integration;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IntegrationService {

  @Autowired
  IntegrationRepository integrationRepository;

  public Flux<Integration> findAll() {
    return integrationRepository.findAll();
  }

  public Flux<Integration> findByName(String name) {
    return integrationRepository.findByName(name);
  }

  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id);
  }

  public Mono<Integration> initialize(String id) {
    return integrationRepository.initialize(id);
  }
}