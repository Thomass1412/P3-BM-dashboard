package com.aau.p3.performancedashboard.service;

import com.aau.p3.performancedashboard.converter.IntegrationConverter;
import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.InternalIntegration;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.request.IntegrationDataSchemaRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationResponse;
import com.aau.p3.performancedashboard.repository.InternalIntegrationRepository;

import com.aau.p3.performancedashboard.repository.IntegrationRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class IntegrationServiceTests {

    @Mock
    private IntegrationDataService integrationDataService;

    @Mock
    private InternalIntegrationRepository internalIntegrationRepository;

    @Mock
    private IntegrationRepository integrationRepository;

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

    @Test
    void testCreateIntegration_Success() {

        String integrationName = validRequest.getName();
        String collectionName = "someCollectionName";

        Mockito.when(integrationRepository.findByName(integrationName)).thenReturn(Mono.empty());
        Mockito.when(integrationDataService.createCollection(validRequest)).thenReturn(Mono.just(collectionName));
        Mockito.when(internalIntegrationRepository.save(Mockito.any(InternalIntegration.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, InternalIntegration.class)));

        try (MockedStatic<IntegrationConverter> mockedConverter = Mockito.mockStatic(IntegrationConverter.class)) {
            IntegrationResponse expectedResponse = new IntegrationResponse("id", integrationName, "type", new Date(), collectionName);
            mockedConverter.when(() -> IntegrationConverter.convertAnyIntegrationToIntegrationResponse(Mockito.any(InternalIntegration.class)))
                .thenReturn(expectedResponse);

            // Act
            Mono<IntegrationResponse> result = integrationService.createIntegration(validRequest);

            // Assert
            StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals(integrationName) && response.getDataCollection().equals(collectionName))
                .verifyComplete();
            }

        Mockito.verify(integrationRepository).findByName(integrationName);
        Mockito.verify(integrationDataService).createCollection(validRequest);
        Mockito.verify(internalIntegrationRepository).save(Mockito.any(InternalIntegration.class));
    }

    @Test
    void testCreateIntegration_NameAlreadyExists(){
        Mockito.when(integrationRepository.findByName(validRequest.getName())).thenReturn(Mono.just(new Integration()));

        // Execute
        Mono<IntegrationResponse> result = integrationService.createIntegration(validRequest);

        // Verify and Assert
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                throwable.getMessage().contains("Integration with name '" + validRequest.getName() + "' already exists."))
            .verify();

        Mockito.verify(integrationRepository).findByName(validRequest.getName());
        Mockito.verify(integrationRepository, Mockito.never()).save(Mockito.any());
    }
}

