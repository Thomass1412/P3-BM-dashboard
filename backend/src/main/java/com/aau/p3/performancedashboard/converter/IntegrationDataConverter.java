package com.aau.p3.performancedashboard.converter;

import java.util.Date;
import java.util.Map;

import org.bson.Document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;
import com.aau.p3.performancedashboard.service.UserService;

import reactor.core.publisher.Mono;

public class IntegrationDataConverter {

    // Logger
    private static final Logger logger = LogManager.getLogger(IntegrationDataConverter.class);

    public static Mono<IntegrationDataResponse> convertDocumentToIntegrationDataResponse(UserService userService, Document document)
        throws IllegalArgumentException {
    IntegrationDataResponse response = new IntegrationDataResponse();

    if (document.isEmpty()) {
        logger.debug("Document is empty");
        throw new ClassCastException("Cannot cast empty document to IntegrationDataResponse");
    }

    Object id = document.get("_id");
    if (id != null) {
        response.setId(id.toString());
    } else {
        logger.debug("'_id' is null in the document");
    }

    Object integrationId = document.get("integrationId");
    if (integrationId != null) {
        response.setIntegrationId(integrationId.toString());
    } else {
        logger.debug("'integrationId' is null in the document");
    }

    Object timestamp = document.get("timestamp");
    if (timestamp instanceof java.util.Date) {
        response.setTimestamp((java.util.Date) timestamp);
    } else {
        logger.debug("'timestamp' is null or not a Date in the document");
    }

    // Handle the 'data' field as a Map
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) document.get("data");
    response.setData(data);

    // Fetch the user from the database based on the ID
    String userId = (String) document.get("userId");
    response.setUserId(userId);
    
    return Mono.just(response);
}

    public static Document convertCreateIntegrationDataRequestToDocument(String integrationId,
            CreateIntegrationDataRequest request, User user) {
        Document document = new Document();
        document.put("integrationId", (String) integrationId);
        document.put("timestamp", (Date) request.getTimestamp());
        document.put("data", (Map<String, Object>) request.getData());
        document.put("userId", (String) user.getId()); // Add the user as a @DocumentReference
        return document;
    }
}
