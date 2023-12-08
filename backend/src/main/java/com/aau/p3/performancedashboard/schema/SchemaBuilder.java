package com.aau.p3.performancedashboard.schema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.schema.JsonSchemaObject.Type;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema.MongoJsonSchemaBuilder;

import com.aau.p3.performancedashboard.payload.request.IntegrationDataSchemaRequest;

/**
 * This class is responsible for building a MongoDB JSON schema based on a list of schema requests.
 * The schema will always include the "_id", "timestamp", and "employee" fields.
 * The "data" field will be built based on the provided schema requests.
 */
public class SchemaBuilder {

    /**
     * Generates a MongoDB JSON schema from a list of schema requests.
     * The schema will include the "_id", "timestamp", and "employee" fields.
     * The "data" field will be built based on the provided schema requests.
     *
     * @param schemaRequests A list of schema requests to build the "data" field of the schema.
     * @return A MongoDB JSON schema built from the provided schema requests.
     * @throws IllegalArgumentException If an invalid type is provided in a schema request.
     */
    public static MongoJsonSchema generateFrom(List<IntegrationDataSchemaRequest> schemaRequests) throws IllegalArgumentException {
        MongoJsonSchemaBuilder builder = MongoJsonSchema.builder();
        List<JsonSchemaProperty> properties = new ArrayList<>();

        // Add the required properties to the list for the schema. (id, timestamp, employee)
        //properties.add(JsonSchemaProperty.named("_id").ofType(Type.stringType()));
        properties.add(JsonSchemaProperty.named("integrationId").ofType(Type.stringType()));
        properties.add(JsonSchemaProperty.named("timestamp").ofType(Type.dateType()));
        //properties.add(JsonSchemaProperty.named("employee").ofType(Type.objectType()));

        // Create a new builder for the data field.
        MongoJsonSchemaBuilder dataBuilder = MongoJsonSchema.builder();
        List<JsonSchemaProperty> dataProperties = new ArrayList<>();
        List<String> dataRequiredProperties = new ArrayList<>();

        // Iterate through the schema requests and add the properties to the list for the schema.
        for (IntegrationDataSchemaRequest request : schemaRequests) {
            String key = request.getName();
            BasicDataEnum type = BasicDataEnum.valueOf(request.getType().toUpperCase());

            switch (type) {
                case TEXT:
                    dataProperties.add(JsonSchemaProperty.string(key));
                    break;
                case NUMBER:
                    dataProperties.add(JsonSchemaProperty.number(key));
                    break;
                case DATE:
                    dataProperties.add(JsonSchemaProperty.timestamp(key));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type: " + type + " for key: " + key + "of types 'TEXT', 'NUMBER', or 'DATE'.");
            }

            // Check if the property is required
            if (request.isRequired()) {
                dataRequiredProperties.add(key);
            }
        }

        // Add the properties to the data builder and build the data schema.
        dataBuilder.properties(dataProperties.toArray(new JsonSchemaProperty[0]));
        dataBuilder.required(dataRequiredProperties.toArray(new String[0]));

        // Build the data schema and add it to the main schema.
        JsonSchemaProperty dataSchema = JsonSchemaProperty.object("data").properties(dataProperties.toArray(new JsonSchemaProperty[0])).required(dataRequiredProperties.toArray(new String[0]));
        properties.add(dataSchema);

        // Add the properties to the main builder.
        builder.properties(properties.toArray(new JsonSchemaProperty[0]));

        // Return the built schema.
        return builder.build();
    }
}

/* 
{
  "name": "bananas",
  "type": "internal",
  "schema": [
    {
      "name": "Brand",
      "type": "text",
      "required": true
    },
    {
      "name": "Price",
      "type": "number",
      "required": true
    },
    {
      "name": "Maturity",
      "type": "text",
      "required": false
    }
  ]
}
*/
/* 
{
  "timestamp": "2023-12-07T19:43:28.087Z",
  "data": {
    "Brand": "El mexican",
    "Price": 2.49,
    "Maturity": "Yellow"
  }
}
 */