package com.aau.p3.performancedashboard.controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.aau.p3.performancedashboard.payload.JWTToken;
import com.aau.p3.performancedashboard.payload.request.LoginRequest;
import com.aau.p3.performancedashboard.security.TokenProvider;
import com.aau.p3.performancedashboard.security.jwt.JWTReactiveAuthenticationManager;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@Tag(name = "Authentication", description = "Authentication Management APIs")
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Logger
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    // Dependencies
    private final TokenProvider tokenProvider;
    private final JWTReactiveAuthenticationManager authenticationManager;

    @Value("${performancedashboard.app.jwtCookieName}")
    private String jwtCookieName;

    // Constructor injection
    public AuthController(TokenProvider tokenProvider, JWTReactiveAuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JWTToken>> authorize(@Valid @RequestBody LoginRequest loginRequest,
            ServerHttpResponse response) {
        // Get the authentication token
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());

        // Authenticate and return both the token and the cookie
        return this.authenticationManager.authenticate(authenticationToken)
                .doOnSuccess(auth -> logger.debug("Authentication success: {}", auth))
                .doOnError(throwable -> {
                    throw new BadCredentialsException("Bad crendentials");
                })
                .map(auth -> {
                    String jwt = tokenProvider.createToken(auth, Optional.empty());
                    ResponseCookie jwtCookie = ResponseCookie.from(jwtCookieName, jwt)
                            .httpOnly(true)
                            .path("/")
                            .build();
                    response.addCookie(jwtCookie); // Add cookie to response
                    return ResponseEntity.ok(new JWTToken(jwt));
                });
    }
}