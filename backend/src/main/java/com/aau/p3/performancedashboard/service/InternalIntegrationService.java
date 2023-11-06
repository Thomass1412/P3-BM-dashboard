package com.aau.p3.performancedashboard.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InternalIntegrationService {

  @Autowired
  InternalIntegrationRepository internalIntegrationRepository;

  public Flux<InternalIntegration> findAll() {
    return internalIntegrationRepository.findAll();
  }

  public Flux<InternalIntegration> findByTitleContaining(String title) {
    return internalIntegrationRepository.findByTitleContaining(title);
  }

  public Mono<InternalIntegration> findById(String id) {
    return internalIntegrationRepository.findById(id);
  }

  public Mono<InternalIntegration> save(InternalIntegration internalIntegration) {
    return internalIntegrationRepository.save(internalIntegration);
  }

  public Mono<InternalIntegration> update(String id, InternalIntegration internalIntegration) {
    return internalIntegrationRepository.findById(id).map(Optional::of).defaultIfEmpty(Optional.empty())
        .flatMap(optionalInternalIntegration -> {
          if (optionalInternalIntegration.isPresent()) {
            internalIntegration.setId(id);
            return internalIntegrationRepository.save(internalIntegration);
          }

          return Mono.empty();
        });
  }

  public Mono<Void> deleteById(String id) {
    return internalIntegrationRepository.deleteById(id);
  }

  public Mono<Void> deleteAll() {
    return internalIntegrationRepository.deleteAll();
  }
}