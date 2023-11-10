package com.aau.p3.performancedashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;

import reactor.core.publisher.Flux;

@Service
public class IntegrationService {

  @Autowired
  IntegrationRepository integrationRepository;

  public Flux<Integration> findAll() {
    return integrationRepository.findAll();
  }
}