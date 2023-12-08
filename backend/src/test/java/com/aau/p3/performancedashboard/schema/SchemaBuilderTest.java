
package com.aau.p3.performancedashboard.schema;

import com.aau.p3.performancedashboard.payload.request.CreateIntegrationRequest;
import com.aau.p3.performancedashboard.payload.request.IntegrationDataSchemaRequest;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaBuilderTest {

    private List<IntegrationDataSchemaRequest> schemaRequests;

    @BeforeEach
    public void setup() {
        CreateIntegrationRequest integrationRequest = new CreateIntegrationRequest();
        integrationRequest.setName("bananas");
        integrationRequest.setType("internal");
        integrationRequest.setSchema(Arrays.asList(
            new IntegrationDataSchemaRequest("Brand", "text", true),
            new IntegrationDataSchemaRequest("Price", "number", true),
            new IntegrationDataSchemaRequest("Maturity", "text", false)
        ));

        schemaRequests = integrationRequest.getSchema();
    }

    @Test
    public void testGenerateFrom() {
        MongoJsonSchema schema = SchemaBuilder.generateFrom(schemaRequests);
        
        // The MongoJsonSchema is different than the input, and has no type safety.
        // This test checks that the 
        // Check that the schema has the correct properties
        assertTrue(schema.toDocument().containsKey("$jsonSchema"));
        Document jsonSchema = schema.toDocument().get("$jsonSchema", org.bson.Document.class);
        assertTrue(jsonSchema.containsKey("properties"));
        Document properties = jsonSchema.get("properties", org.bson.Document.class);
        assertTrue(properties.containsKey("data"));
        Document dataProperties = properties.get("data", org.bson.Document.class).get("properties", org.bson.Document.class);
        assertTrue(dataProperties.containsKey("Brand"));
        assertTrue(dataProperties.containsKey("Price"));
        assertTrue(dataProperties.containsKey("Maturity"));

        // Check that the required properties are correct
        assertTrue(jsonSchema.containsKey("required"));
        List<?> rawRequiredProperties = jsonSchema.get("required", List.class);
        List<String> requiredProperties = rawRequiredProperties.stream()
            .filter(obj -> obj instanceof String)
            .map(obj -> (String) obj)
            .collect(Collectors.toList());
        assertTrue(requiredProperties.contains("Brand"));
        assertTrue(requiredProperties.contains("Price"));
        assertFalse(requiredProperties.contains("Maturity"));
    }
}