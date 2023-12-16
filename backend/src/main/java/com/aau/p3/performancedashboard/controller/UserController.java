package com.aau.p3.performancedashboard.controller;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.converter.UserConverter;
import com.aau.p3.performancedashboard.exceptions.NotFoundException;
import com.aau.p3.performancedashboard.model.Authority;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.RegisterRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.UserResponse;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.AuthorityConstant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.RequestBody;
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
    private final ReactiveMongoTemplate mongoTemplate;

    // Constructor injection
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserConverter userConverter,
            AuthorityRepository authorityRepository, ObjectMapper objectMapper, ReactiveMongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
        this.authorityRepository = authorityRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
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
        logger.debug("Registering user: {}", registerRequest);

        try {
            String json = objectMapper.writeValueAsString(registerRequest);
            logger.debug("RegisterRequest: " + json);
        } catch (JsonProcessingException e) {
            logger.error("Error converting RegisterRequest to JSON", e);
        }

        return userRepository.existsByLogin(registerRequest.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Login is already taken!"));
                    } else {
                        User user = new User();

                        user.setLogin(registerRequest.getUsername());
                        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                        user.setDisplayName(registerRequest.getDisplayName());
                        user.setEmail(registerRequest.getEmail());
                        user.setAchievements(new LinkedList<>());

                        List<String> authorities = registerRequest.getAuthorities();

                        if (authorities != null) {
                            return Flux.fromIterable(authorities)
                                    .flatMap(authorityName -> {
                                        Query query = new Query();
                                        query.addCriteria(Criteria.where("name").is(authorityName));
                                        return mongoTemplate.findOne(query, Authority.class, "authorities")
                                                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                                                        "Authority " + authorityName + " does not exist")));
                                    })
                                    .collectList()
                                    .flatMap(validAuthorities -> {
                                        user.setAuthorities(new HashSet<>(validAuthorities));
                                        return Mono.just(user);
                                    });
                        } else {
                            return authorityRepository.findByName(AuthorityConstant.AGENT)
                                    .flatMap(agentAuthority -> {
                                        user.setAuthorities(new HashSet<>());
                                        user.getAuthorities().add(agentAuthority);
                                        return Mono.just(user);
                                    });
                        }
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
     * Retrieves a user by the provided user ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a Mono containing the ResponseEntity with the user response if found,
     *         or ResponseEntity.notFound() if the user is not found
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR') or hasRole('ROLE_AGENT')")
    public Mono<UserResponse> getUserById(@PathVariable(value = "id") String userId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUsername = userDetails.getUsername();

        // Check if the user is an agent and is trying to access another user's data
        // If so, return an error. Only admins and supervisors can access other users'.
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"))
                            && !user.getLogin().equals(currentUsername)) {
                        return Mono.error(new AccessDeniedException("Access denied."));
                    }

                    UserResponse userResponse = userConverter.convertToResponse(user);
                    return Mono.just(userResponse);
                })
                .switchIfEmpty(Mono.error(new NotFoundException("User not found.")));
    }
}
