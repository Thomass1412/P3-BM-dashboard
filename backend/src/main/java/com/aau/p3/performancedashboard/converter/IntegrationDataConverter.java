package com.aau.p3.performancedashboard.converter;

import java.util.Date;
import java.util.Map;

import org.bson.Document;

import com.aau.p3.performancedashboard.payload.request.CreateIntegrationDataRequest;
import com.aau.p3.performancedashboard.payload.response.IntegrationDataResponse;

public class IntegrationDataConverter {

    public static IntegrationDataResponse convertDocumentToIntegrationDataResponse(Document document)
            throws IllegalArgumentException {
        IntegrationDataResponse response = new IntegrationDataResponse();

        if (document.isEmpty()) {
            throw new ClassCastException("Cannot cast empty document to IntegrationDataResponse");
        }

        response.setId(document.get("_id").toString());
        response.setIntegrationId(document.get("integrationId").toString());
        response.setTimestamp((java.util.Date) document.get("timestamp"));
        
        // Handle the 'data' field as a Map
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) document.get("data");
        response.setData(data);

        return response;
    }

    public static Document convertCreateIntegrationDataRequestToDocument(String integrationId, CreateIntegrationDataRequest request) {
        Document document = new Document();
        document.put("integrationId", (String) integrationId);
        document.put("timestamp", (Date) request.getTimestamp());
        document.put("data", (Map<String, Object>) request.getData());
        return document;
    }
}
