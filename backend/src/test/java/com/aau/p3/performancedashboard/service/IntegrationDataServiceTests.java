package com.aau.p3.performancedashboard.service;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static reactor.core.publisher.Mono.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class IntegrationDataServiceTests {

    @Mock
    private IntegrationService integrationService;

    @Mock
    private ReactiveMongoOperations mongoOperations;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private MongoDatabase mongoDatabase;

    @Mock
    private UserService userService;

    private IntegrationDataService integrationDataService;

    @BeforeEach
    void setUp() {
        integrationDataService = new IntegrationDataService(integrationService, mongoOperations, mongoTemplate, mongoDatabase, userService);
    }

    @Test
void testSaveIntegrationData_UserExists() {
    // Arrange
    String integrationId = "integrationId";
    String userId = "userId";
    Date timestamp = new Date();
    Map<String, Object> data = new HashMap<>();
    CreateIntegrationDataRequest request = new CreateIntegrationDataRequest();
    request.setUserId(userId);
    request.setTimestamp(timestamp);
    request.setData(data);

    IntegrationDataResponse expectedResponse = new IntegrationDataResponse();
    expectedResponse.setId("responseId");
    expectedResponse.setIntegrationId(integrationId);
    expectedResponse.setUserId(userId);
    expectedResponse.setTimestamp(timestamp);
    expectedResponse.setData(data);

    User user = new User();
    user.setId(userId);

    Integration integration = new Integration();
    integration.setType("internal"); // Set the type to avoid NullPointerException
    when(integrationService.findById(integrationId)).thenReturn(Mono.just(integration));

    when(userService.findById(userId)).thenReturn(Mono.just(user));

    // Act
    Mono<IntegrationDataResponse> result = integrationDataService.saveIntegrationData(integrationId, request);

    // Assert
    StepVerifier.create(result)
        .expectNext(expectedResponse)
        .verifyComplete();

    verify(userService).findById(userId);
    verify(integrationService).findById(integrationId);
}

}
