package com.aau.p3.performancedashboard.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a request to create a new IntegrationDataSchema.
 * It includes properties for the name, type, and whether the schema is required.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationDataSchemaRequest {

    /**
     * The name of the IntegrationDataSchema.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @NotBlank(message = "Name cannot be blank")
    @Schema(name = "name", example = "Brand", required = true)
    private String name;

    /**
     * The type of the IntegrationDataSchema.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @NotBlank(message = "Type cannot be blank")
    @Schema(name = "type", example = "text", required = true)
    private String type;

    /**
     * Whether the IntegrationDataSchema is required.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @NotNull(message = "Required cannot be null")
    @Schema(name = "required", example = "true")
    private boolean required;
}
