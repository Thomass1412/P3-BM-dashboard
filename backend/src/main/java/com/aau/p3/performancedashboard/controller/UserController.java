package com.aau.p3.performancedashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.converter.UserConverter;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.RegisterRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.UserResponse;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.AuthorityConstant;

import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.aau.p3.performancedashboard.repository.AuthorityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management APIs")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    // Logger
    private static final Logger logger = LogManager.getLogger(UserController.class);

    // Dependencies
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final AuthorityRepository authorityRepository;
    private final ObjectMapper objectMapper;

    // Constructor injection
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserConverter userConverter,
            AuthorityRepository authorityRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
        this.authorityRepository = authorityRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Registers a new user.
     *
     * @param registerRequest the register request containing user information
     * @return a Mono of ResponseEntity containing the user response
     */
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully registered the user", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Bad request. User registration failed.", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.debug("Registering user with login: " + registerRequest.getLogin());

        try {
            String json = objectMapper.writeValueAsString(registerRequest);
            logger.debug("RegisterRequest: " + json);
        } catch (JsonProcessingException e) {
            logger.error("Error converting RegisterRequest to JSON", e);
        }

        return userRepository.existsByLogin(registerRequest.getLogin())

                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Login is already taken!"));
                    } else {
                        User user = new User();

                        user.setLogin(registerRequest.getLogin());
                        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                        user.setDisplayName(registerRequest.getDisplayName());
                        user.setEmail(registerRequest.getEmail());

                        List<String> authorities = registerRequest.getAuthorities();

                        if (authorities != null) {
                            return Flux.fromIterable(authorities)
                                    .filter(authority -> AuthorityConstant.ADMIN.equals(authority)
                                            || AuthorityConstant.SUPERVISOR.equals(authority)
                                            || AuthorityConstant.AGENT.equals(authority)
                                            || AuthorityConstant.TV.equals(authority))
                                    .flatMap(authorityRepository::findByName)
                                    .collectList()
                                    .map(authorityList -> {
                                        user.setAuthorities(new HashSet<>(authorityList));
                                        return user;
                                    });
                        }

                        return Mono.just(user);
                    }
                })
                .flatMap(user -> userRepository.save(user).thenReturn(user))
                .doOnSuccess(user -> logger.debug("User registered successfully: {}", user))
                .doOnError(error -> logger.error("Error occurred while registering user: {}", error.getMessage()))
                .map(userConverter::convertToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Gets all users.
     *
     * @return a Flux of ResponseEntity containing the user responses
     */
    @Operation(summary = "Get user by ID", description = "Fetches a user by the provided user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user", content = {
                    @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable(value = "id") String userId) {
        return userRepository.findById(userId)
                .doOnSuccess(user -> logger.debug("User fetched successfully: {}", user))
                .doOnError(error -> logger.error("Error occurred while fetching user: {}", error.getMessage()))
                .map(userConverter::convertToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
