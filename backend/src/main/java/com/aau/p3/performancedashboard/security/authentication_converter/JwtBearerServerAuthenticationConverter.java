package com.aau.p3.performancedashboard.security.authentication_converter;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import com.aau.p3.performancedashboard.security.SecurityUtils;
import com.aau.p3.performancedashboard.security.TokenProvider;

import reactor.core.publisher.Mono;

public class JwtBearerServerAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String, String> isolateBearerValue = authValue -> authValue.substring(BEARER.length(),
            authValue.length());

    private final TokenProvider tokenProvider;

    @Autowired
    public JwtBearerServerAuthenticationConverter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Converts a ServerWebExchange into an Authentication object.
     * This method extracts the token from the request, validates it, and returns the corresponding Authentication object.
     *
     * @param serverWebExchange the ServerWebExchange object representing the HTTP request and response
     * @return a Mono containing the Authentication object if the token is valid, or an empty Mono if the token is invalid or not present
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .map(SecurityUtils::getTokenFromRequest)
                .filter(Objects::nonNull)
                .filter(matchBearerLength)
                .map(isolateBearerValue)
                .filter(StringUtils::hasText)
                .map(tokenProvider::getAuthentication)
                .filter(Objects::nonNull);
    }
}
