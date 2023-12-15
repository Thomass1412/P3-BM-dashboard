package com.aau.p3.performancedashboard.schema;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.schema.JsonSchemaObject.Type;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema.MongoJsonSchemaBuilder;

import com.aau.p3.performancedashboard.payload.request.IntegrationDataSchemaRequest;

/**
 * This class is responsible for building a MongoDB JSON schema based on a list
 * of schema requests.
 * The schema will always include the "_id", "timestamp", and "employee" fields.
 * The "data" field will be built based on the provided schema requests.
 */
public class SchemaBuilder {

  // Logger
  private static final Logger logger = LoggerFactory.getLogger(SchemaBuilder.class);

  /**
   * Generates a MongoDB JSON schema from a list of schema requests.
   * The schema will include the "_id", "timestamp", and "employee" fields.
   * The "data" field will be built based on the provided schema requests.
   *
   * @param schemaRequests A list of schema requests to build the "data" field of
   *                       the schema.
   * @return A MongoDB JSON schema built from the provided schema requests.
   * @throws IllegalArgumentException If an invalid type is provided in a schema
   *                                  request.
   */
  public static MongoJsonSchema generateFrom(List<IntegrationDataSchemaRequest> schemaRequests)
      throws IllegalArgumentException, RuntimeException {
    logger.info("Generating MongoDB JSON schema from schema requests...");

    // Build the properties for the "data" object.
    List<JsonSchemaProperty> dataProperties = buildProperties(schemaRequests);

    // Build the "data" object.
    JsonSchemaProperty dataSchema = JsonSchemaProperty.object("data")
        .properties(dataProperties.toArray(new JsonSchemaProperty[0]));

    // Build the properties for the main schema.
    List<JsonSchemaProperty> properties = new ArrayList<>();
    properties.add(JsonSchemaProperty.named("integrationId").ofType(Type.stringType()));
    properties.add(JsonSchemaProperty.named("timestamp").ofType(Type.dateType()));
    properties.add(dataSchema); // Add the "data" object to the properties.

    MongoJsonSchemaBuilder schemaBuilder = MongoJsonSchema.builder();
    schemaBuilder.properties(properties.toArray(new JsonSchemaProperty[0]));

    logger.debug("MongoDB JSON schema generated successfully.");

    MongoJsonSchema schema = schemaBuilder.build();
    if (schema == null) {
      throw new RuntimeException("Failed to generate MongoDB JSON schema.");
    }
    return schema;
  }

  /**
   * Builds a list of JsonSchemaProperty objects based on a list of IntegrationDataSchemaRequest objects.
   *
   * @param schemaRequests the list of IntegrationDataSchemaRequest objects
   * @return the list of JsonSchemaProperty objects
   * @throws IllegalArgumentException if any of the schema requests are invalid
   */
  private static List<JsonSchemaProperty> buildProperties(List<IntegrationDataSchemaRequest> schemaRequests)
      throws IllegalArgumentException {
    List<JsonSchemaProperty> properties = new ArrayList<>();

    for (IntegrationDataSchemaRequest request : schemaRequests) {
      JsonSchemaProperty property = buildPropertyFromRequest(request);
      properties.add(property);
    }

    return properties;
  }

  /**
   * Represents a property in a JSON schema.
   */
  private static JsonSchemaProperty buildPropertyFromRequest(IntegrationDataSchemaRequest request)
      throws IllegalArgumentException, RuntimeException {
    String key = request.getName();
    String typeString = request.getType().toUpperCase();

    BasicDataEnum type = getType(typeString, key);

    JsonSchemaProperty property = getPropertyBasedOnType(type, key);

    if (request.isRequired()) {
      property = JsonSchemaProperty.required(property);
    }

    return property;
  }

  /**
   * Enum representing the basic data types.
   * The possible values are 'TEXT', 'NUMBER', and 'DATE'.
   */
  private static BasicDataEnum getType(String typeString, String key) throws IllegalArgumentException {
    try {
      return BasicDataEnum.valueOf(typeString);
    } catch (IllegalArgumentException e) {
      String errorMessage = "Invalid type: " + typeString + " for key: " + key
          + ". Expected 'TEXT', 'NUMBER', or 'DATE'.";
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage, e);
    }
  }

  /**
   * Represents a property in a JSON schema.
   */
  private static JsonSchemaProperty getPropertyBasedOnType(BasicDataEnum type, String key)
      throws IllegalArgumentException {
    switch (type) {
      case TEXT:
        return JsonSchemaProperty.string(key);
      case NUMBER:
        return JsonSchemaProperty.number(key);
      case DATE:
        return JsonSchemaProperty.timestamp(key);
      default:
        String errorMessage = "Unexpected type: " + type + " for key: " + key;
        logger.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
  }
}

/*
 * {
 * "name": "bananas",
 * "type": "internal",
 * "schema": [
 * {
 * "name": "Brand",
 * "type": "text",
 * "required": true
 * },
 * {
 * "name": "Price",
 * "type": "number",
 * "required": true
 * },
 * {
 * "name": "Maturity",
 * "type": "text",
 * "required": false
 * }
 * ]
 * }
 */
/*
 * {
 * "timestamp": "2023-12-07T19:43:28.087Z",
 * "data": {
 * "Brand": "El mexican",
 * "Price": 2.49,
 * "Maturity": "Yellow"
 * }
 * }
 */