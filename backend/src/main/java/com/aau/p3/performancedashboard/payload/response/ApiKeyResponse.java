package com.aau.p3.performancedashboard.payload.response;

import java.util.Date;

import com.aau.p3.performancedashboard.model.ApiKey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiKeyResponse {
    /**
     * The key of the {@link ApiKey}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @NotBlank(message = "Key cannot be blank")
    @Schema(name = "key", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfQUdFTlQiLCJleHAiOjE3MDI2NjAxNDQsImlhdCI6MTcwMjU3Mzc0NH0.ZVAG2v0oqCbbpiJfwSnxMy1ptHa-uw3KVlwOORxQC-PBEgM0DZi1cmdTTqDc8tzu-LpJAuPNZPhjKKeAz_4zxA")
    private String key;
    
    /**
     * The name of the {@link ApiKey}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be blank.
     */
    @NotBlank(message = "Name cannot be blank")
    @Schema(name = "name", example = "myApiApp")
    private String name;

    /**
     * The creator of the {@link ApiKey}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     * This property must not be null.
     */
    @NotNull(message = "Creator cannot be null")
    @Schema(name = "creator", example = "John Doe")
    private String creator;
    private String creatorEmail;
    /**
     * The creation date of the {@link ApiKey}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(name = "creationDate", example = "2021-05-01T12:00:00.000Z")
    private final Date creationDate;

    /**
     * The expiration date of the {@link ApiKey}.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(name = "expirationDate", example = "2022-05-01T12:00:00.000Z")
    private final Date expirationDate;
    
}
