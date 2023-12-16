package com.aau.p3.performancedashboard.service;

import com.aau.p3.performancedashboard.converter.ApiKeyConverter;
import com.aau.p3.performancedashboard.model.ApiKey;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.response.ApiKeyResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.repository.ApiKeyRepository;
import com.aau.p3.performancedashboard.security.TokenProvider;

import java.util.Collections;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Optional;

@Service
public class ApiKeyService {

    // Logger
    private static final Logger logger = LogManager.getLogger(ApiKeyService.class);

    // Dependencies
    private final ApiKeyRepository apiKeyRepository;
    private final TokenProvider tokenProvider;
    private final ApiKeyConverter apiKeyConverter;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository, TokenProvider tokenProvider, ApiKeyConverter apiKeyConverter) {
        this.apiKeyRepository = apiKeyRepository;
        this.tokenProvider = tokenProvider;
        this.apiKeyConverter = apiKeyConverter;
    }

    public Mono<Page<ApiKeyResponse>> findAllBy(Pageable pageable) {
        return apiKeyRepository.findAllBy(pageable)
            .flatMap(apiKey -> Mono.just(apiKeyConverter.convertToResponse(apiKey)))
            .collectList()
            .zipWith(apiKeyRepository.count())
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }


    public Mono<ApiKeyResponse> createApiKey(User user, String name, Date expirationDate) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword(), Collections.singletonList(authority));
        String token = tokenProvider.createToken(authentication, Optional.of(expirationDate));

        ApiKey apiKey = new ApiKey(token, name, user, new Date(), expirationDate);

        return apiKeyRepository.save(apiKey)
            .flatMap(apiKeySaved -> Mono.just(apiKeyConverter.convertToResponse(apiKeySaved)))
            .doOnSuccess(apiKeyResponse -> logger.debug("API key created: {}", apiKeyResponse))
            .doOnError(error -> logger.error("API key creation failed: {}", error.getMessage()));
    }

    public Mono<Boolean> validateApiKey(String key) {
        // Fetch the API key from the repository and check if it exists and is not expired
        return apiKeyRepository.findById(key)
            .map(apiKey -> !apiKey.getExpirationDate().before(new Date()))
            .defaultIfEmpty(false);
    }

    public Mono<MessageResponse> revokeApiKey(String key) {
        return apiKeyRepository.deleteById(key)
            .then(Mono.fromCallable(() -> {
                return new MessageResponse("API key revoked successfully.");
            }))
            .defaultIfEmpty(new MessageResponse("API key not found."));
    }
}