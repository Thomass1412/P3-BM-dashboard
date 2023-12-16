package com.aau.p3.performancedashboard.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;

import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.response.ApiKeyResponse;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.PageableResponse;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.service.ApiKeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import reactor.core.publisher.Mono;

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication", description = "Authentication Management APIs")
@RestController
@RequestMapping("/auth/api-keys/pageable")
class ApiKeysController {

    // Logger
    private static final Logger logger = LogManager.getLogger(ApiKeysController.class);

    // Dependencies
    private final ApiKeyService apiKeyService;

    // Constructor injection
    public ApiKeysController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Operation(summary = "Retrieve a page of API keys", description = "Fetches a page of API keys based on the provided page number and size.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a page of API keys", content = {
                    @Content(schema = @Schema(implementation = PageableResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Bad request. No API keys found.", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Page<ApiKeyResponse>> getAllApiKeysBy(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        logger.debug("getAllApiKeysBy() called with: page = [" + page + "], size = [" + size + "]");
        // Extract the request parameters into a Pageable object.
        Pageable pageable = PageRequest.of(page, size);
        // Fetch the page of API keys.
        return apiKeyService.findAllBy(pageable);
    }

}

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authentication", description = "Authentication Management APIs")
@RestController
@RequestMapping("/auth/api-key")
public class ApiKeyController {

    // Logger
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger(ApiKeysController.class);

    // Dependencies
    private final ApiKeyService apiKeyService;
    private final UserRepository userRepository;

    // Constructor injection
    public ApiKeyController(ApiKeyService apiKeyService, UserRepository userRepository) {
        this.apiKeyService = apiKeyService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a new API key", description = "Creates a new API key based on the provided name and expiration date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved an API key response", content = {
                    @Content(schema = @Schema(implementation = ApiKeyResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ApiKeyResponse>> createApiKey(
            Authentication authentication,
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expirationDate) {

        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                .getUsername();

        return userRepository.findByLogin(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found with username: " + username)))
                .flatMap((User user) -> {
                    Date date = Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant());
                    return apiKeyService.createApiKey(user, name, date);
                })
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Validate an API key", description = "Validates an API key based on the provided key.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully validated an API key", content = {
                    @Content(schema = @Schema(implementation = Boolean.class), mediaType = "application/json")
            })
    })
    @GetMapping(path = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Boolean>> validateApiKey(@PathVariable String key) {
        return apiKeyService.validateApiKey(key)
                .map(isValid -> ResponseEntity.ok(isValid));
    }

    @Operation(summary = "Revoke an API key", description = "Revokes an API key based on the provided key.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully revoked an API key", content = {
                    @Content(schema = @Schema(implementation = MessageResponse.class), mediaType = "application/json")
            })
    })
    @DeleteMapping(path = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<MessageResponse>> revokeApiKey(@PathVariable String key) {
        return apiKeyService.revokeApiKey(key)
                .map(messageResponse -> ResponseEntity.ok(messageResponse));
    }
}
