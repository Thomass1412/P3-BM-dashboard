package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;
import com.aau.p3.performancedashboard.dto.IntegrationDTO;
import com.aau.p3.performancedashboard.dto.IntegrationMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IntegrationService {

  @Autowired
  IntegrationRepository integrationRepository;

  @Autowired
  InternalIntegrationRepository internalIntegrationRepository;

  public Flux<Integration> findAll() {
    // Find all integrations and convert to DTO
    return integrationRepository.findAll();
  }

  // POST to create integration in DB
  public Mono<Integration> saveIntegration(Integration integration) {

    IntegrationDataService integrationDataService = new IntegrationDataService();
    // Save the converted class to DB and wrap response in a Mono
    InternalIntegration ie = new InternalIntegration(integration.getName());
    ie.setDataCollection(integrationDataService.createCollection(ie.getName() + "-data"));
    Mono<Integration> res = internalIntegrationRepository.save(ie).map(Integration.class::cast);
    return res;
  }

  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id);
  }

}