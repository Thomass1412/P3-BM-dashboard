package com.aau.p3.performancedashboard.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
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

  public Flux<Integration> findByTitleContaining(String title) {
    return integrationRepository.findByTitleContaining(title);
  }

  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id);
  }

  public Mono<Integration> save(Integration integration) {
    return integrationRepository.save(integration);
  }

  public Mono<Integration> update(String id, Integration integration) {
    return integrationRepository.findById(id).map(Optional::of).defaultIfEmpty(Optional.empty())
        .flatMap(optionalIntegration -> {
          if (optionalIntegration.isPresent()) {
            integration.setId(id);
            return integrationRepository.save(integration);
          }

          return Mono.empty();
        });
  }
  

  public Mono<Void> deleteById(String id) {
    return integrationRepository.deleteById(id);
  }

  public Mono<Void> deleteAll() {
    return integrationRepository.deleteAll();
  }
}