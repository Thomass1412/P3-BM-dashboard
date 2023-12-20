package com.aau.p3.performancedashboard.payload.request;

import java.util.List;

import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import com.aau.p3.performancedashboard.integrationSchema.IntegrationDataSchema;
import com.aau.p3.performancedashboard.integrationSchema.SchemaBuilder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to create an integration.
 * This class uses the @Data annotation from Lombok to generate getters, setters, toString, hashCode, and equals methods.
 * This class uses the @AllArgsConstructor annotation from Lombok to generate a constructor with all arguments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIntegrationRequest {
    
    /**
     * The name of the integration.
     * This field uses the @NotBlank annotation from the Jakarta Bean Validation API to validate that the name is not blank.
     * This field uses the @Size annotation from the Jakarta Bean Validation API to validate that the name is between 3 and 25 characters.
     * This field uses the @Schema annotation from the Swagger API to provide a name, example, and requirement for the field.
     *
     * @param name The name of the integration. Must be between 3 and 25 characters.
     */
    @NotBlank(message = "The integration name is required.")
    @Size(min = 3, max = 25, message = "The name must be from 3 to 25 characters.")
    @Schema(name = "name", example = "sales", required = true)
    private String name;

    /**
     * The type of the integration.
     * This field uses the @NotBlank annotation from the Jakarta Bean Validation API to validate that the type is not blank.
     * This field uses the @Schema annotation from the Swagger API to provide a name, example, and requirement for the field.
     *
     * @param type The type of the integration.
     */
    @NotBlank(message = "The integration type is required.")
    @Schema(name = "type", example = "internal", required = true)
    private String type;

    @NotEmpty(message = "The integration schema is required.")
    @Valid
    private List<IntegrationDataSchema> schema;

    public MongoJsonSchema generateSchema() throws IllegalArgumentException {
        return SchemaBuilder.generateFrom(this.schema);
    }
}
