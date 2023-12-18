package com.aau.p3.performancedashboard.service;

import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.request.IntegrationDataSchemaRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationResponse;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class IntegrationServiceTests {

    @Mock
    private IntegrationDataService integrationDataService;

    @Mock
    private InternalIntegrationRepository internalIntegrationRepository;

    @InjectMocks
    private IntegrationService integrationService;

    private CreateIntegrationRequest validRequest;
    private CreateIntegrationRequest invalidRequest;

    @BeforeEach
    void setUp() {
        IntegrationDataSchemaRequest schemaRequest = new IntegrationDataSchemaRequest();
        schemaRequest.setName("Brand");
        schemaRequest.setType("text");
        schemaRequest.setRequired(true);

        List<IntegrationDataSchemaRequest> schemaList = new ArrayList<>();
        schemaList.add(schemaRequest);

        // Initialize requests with valid and invalid data

        validRequest = new CreateIntegrationRequest("ValidName", "internal", schemaList) ; 
        invalidRequest = new CreateIntegrationRequest("InvalidName", "external", schemaList);
    }

    @Test
    void testCreateInternalIntegration_Success() {

        String collectionName = "someCollectionName";

        Mono<String> collectionCreationMono = Mono.just(collectionName);
        Mockito.when(integrationDataService.createCollection(validRequest)).thenReturn(collectionCreationMono);
    
        Mockito.when(internalIntegrationRepository.save(Mockito.any(InternalIntegration.class)))
               .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, InternalIntegration.class)));
    
        Mono<IntegrationResponse> result = integrationService.createInternalIntegration(validRequest);
    
        StepVerifier.create(result)
            .expectNextMatches(response -> response.getName().equals(validRequest.getName()) && response.getType().equals("internal"))
            .verifyComplete();
    
        Mockito.verify(integrationDataService).createCollection(validRequest);
        // Verifying save method was called with any instance of InternalIntegration
        Mockito.verify(internalIntegrationRepository).save(Mockito.any(InternalIntegration.class));
    }
    

    @Test
    void testCreateInternalIntegration_FailureUnsupportedType() {
        // Execute
        Mono<IntegrationResponse> result = integrationService.createInternalIntegration(invalidRequest);

        // Verify and Assert
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                throwable.getMessage().contains("Integration type 'external' is not supported"))
            .verify();

        Mockito.verify(integrationDataService, Mockito.never()).createCollection(Mockito.any());
    }
}

