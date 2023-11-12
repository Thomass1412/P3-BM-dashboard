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

  public Flux<IntegrationDTO> findAll() {
    return integrationRepository.findAll().map(integration -> IntegrationMapper.toDTO(integration));
  } 

  public Mono<Integration> saveIntegration(IntegrationDTO integrationDTO) {
    System.out.println(integrationDTO);

    System.out.println("INTERNAL");
    InternalIntegration internalIntegration = IntegrationMapper.toInternalIntegration(integrationDTO);
    Mono<Integration> res = Mono.just(internalIntegration).flatMap(ie -> internalIntegrationRepository.save(ie));
    return res;
  }

}


// return InternalIntegrationRepository.save(ie.subscribe(ie -> ie)).subscribe(IntegrationMapper::toDTO);
