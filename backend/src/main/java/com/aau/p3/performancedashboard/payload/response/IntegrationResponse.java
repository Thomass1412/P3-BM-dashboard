package com.aau.p3.performancedashboard.payload.response;

import com.aau.p3.performancedashboard.model.Integration;

import java.util.Date;

import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a response for managing {@link Integration} instances.
 * This class is used to encapsulate the data returned from requests related to {@link Integration} instances.
 * This class uses the {@link Schema} annotation from Swagger to provide a description for the model and its properties.
 */
@RequiredArgsConstructor
public class IntegrationResponse {
    /**
     * The ID of the {@link Integration}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be null.
     */
    @Id
    @NotNull(message = "ID cannot be null")
    @Schema(name = "id", example = "655a40f451a6f25c2b736e30", required = true)
    @Getter
    private final String id;

    /**
     * The name of the {@link Integration}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @Getter
    @NotBlank(message = "Name cannot be blank")
    @Schema(name = "name", example = "sales", required = true)
    private final String name;

    /**
     * The type of the {@link Integration}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @Getter
    @NotBlank(message = "Type cannot be blank")
    @Schema(name = "type", example = "internal", required = true)
    private final String type;

    /**
     * The last updated timestamp of the {@link Integration}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Getter
    @Schema(name = "lastUpdated", example = "2021-05-01T12:00:00.000Z", required = true)
    private final Date lastUpdated;

    /**
     * The data collection of the {@link Integration}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Getter
    @Schema(name = "dataCollection", example = "sales-db", required = true)
    private final String dataCollection;
}
