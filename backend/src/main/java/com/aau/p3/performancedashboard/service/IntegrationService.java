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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class IntegrationService {

      private static Logger logger = LogManager.getLogger(IntegrationService.class.getName());

  @Autowired
  IntegrationRepository integrationRepository;

  @Autowired
  InternalIntegrationRepository internalIntegrationRepository;

  public Flux<Integration> findAll() {
    // Find all integrations and convert to DTO
    return integrationRepository.findAll();
  }

  // POST to create integration in DB
  public Mono<Integration> saveIntegration(String name, String type) throws Exception {
    String integrationType = type;
    
    // If an integration with the name already exists
    if(null != integrationRepository.findByName(name).block()) {
      return Mono.error(new IllegalArgumentException("Integration with name '" + name + "'' already exists."));
    }
    


    System.out.println(integrationType);
    if(type.equals("internal")) {
      logger.info("If internal");
      IntegrationDataService integrationDataService = new IntegrationDataService();
      InternalIntegration ie = new InternalIntegration(name);
      ie.setDataCollection(integrationDataService.createCollection(ie.getName() + "-data"));
      Mono<Integration> res = internalIntegrationRepository.save(ie).map(Integration.class::cast);
      return res;
    }

    return Mono.error(new Exception("Something went wrong saving the integration"));
  }

  public Mono<Integration> findById(String id) {
    return integrationRepository.findById(id);
  }

  public Mono<Integration> findByName(String name) {
    return integrationRepository.findByName(name);
  }

}