package com.aau.p3.performancedashboard.converter;

import java.util.Date;

import org.bson.Document;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.payload.response.IntegrationResponse;

import jakarta.validation.Valid;

public class IntegrationConverter {
    
    public static IntegrationResponse convertDocumentToIntegrationResponse(Document document) throws ClassCastException {
        Object lastUpdatedObj = document.get("lastUpdated");
        Date lastUpdated = null;

        // Ensure that the lastUpdated property is of type Date
        if (lastUpdatedObj instanceof Date) {
            lastUpdated = (Date) lastUpdatedObj; // Cast the object to a Date
        } else {
            throw new ClassCastException("lastUpdated is not of type Date");
        }
        
        return new IntegrationResponse(
                (document.get("_id").toString()),
                document.get("name").toString(),
                document.get("type").toString(),
                lastUpdated,
                document.get("dataCollection").toString());
    }

    public static <T extends Integration> IntegrationResponse convertAnyIntegrationToResponse(T integration) {
        return new IntegrationResponse(
                integration.getId(),
                integration.getName(),
                integration.getType(),
                integration.getLastUpdated(),
                integration.getDataCollection());
    }

    public static Document convertIntegrationResponseToDocument(IntegrationResponse integrationResponse) {
        Document document = new Document();
        document.put("_id", (String) integrationResponse.getId());
        document.put("name", (String) integrationResponse.getName());
        document.put("type", (String) integrationResponse.getType());
        document.put("lastUpdated", (Date) integrationResponse.getLastUpdated());
        document.put("dataCollection",(String) integrationResponse.getDataCollection());
        return document;
    }

    public static <T extends Integration> IntegrationResponse convertAnyIntegrationToIntegrationResponse(@Valid T integration) {
        return new IntegrationResponse(
                integration.getId(),
                integration.getName(),
                integration.getType(),
                integration.getLastUpdated(),
                integration.getDataCollection());
    }
}