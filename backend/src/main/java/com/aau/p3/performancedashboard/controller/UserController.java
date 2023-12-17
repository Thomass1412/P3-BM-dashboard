package com.aau.p3.performancedashboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.payload.request.RegisterRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.PageableResponse;
import com.aau.p3.performancedashboard.payload.response.UserResponse;
import com.aau.p3.performancedashboard.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches a page of users based on the provided page number and size.
     *
     * @param page the page number to retrieve
     * @param size the number of users per page
     * @return a page of users
     */
    @Operation(summary = "Retrieve a page of users", description = "Fetches a page of users based on the provided page number and size.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a page of users", content = {
                    @Content(schema = @Schema(implementation = PageableResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Bad request. No users found.", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping(path = "/pageable", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public Mono<Page<UserResponse>> getUsersBy(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return userService.findAllBy(pageable);
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
            }),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.debug("Registering user: {}", registerRequest);

        return userService.register(registerRequest)
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
            }),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR') or hasRole('ROLE_AGENT')")
    public Mono<UserResponse> getUserById(@PathVariable(value = "userId") String userId,
            Authentication authentication) {
        return userService.getUserById(userId, authentication);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to be deleted
     * @return a Mono of ResponseEntity<Void> indicating the result of the deletion
     *         operation
     */
    @Operation(summary = "Delete a user by ID", description = "Deletes a user if the user has the 'ROLE_ADMIN' authority and the user is found. If the user is not found, it returns a 'User not found.' error.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted the user", content = {
                    @Content(schema = @Schema(implementation = MessageResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {
                    @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
public Mono<ResponseEntity<MessageResponse>> deleteUserById(@PathVariable(value = "userId") String userId) {
        return userService.deleteUserById(userId)
                        .then(Mono.just(new ResponseEntity<MessageResponse>(new MessageResponse("User deleted successfully"), HttpStatus.OK)))
                        .onErrorResume(e -> Mono.just(new ResponseEntity<MessageResponse>(new MessageResponse("User not found"), HttpStatus.NOT_FOUND)));
}

    @Operation(summary = "Set user access level", description = "Allows an admin to set the access level of another user. Takes a user ID as a path variable and a new role as a request parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User access level set successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content) })
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Mono<ResponseEntity<UserResponse>> setUserAccess(
            @PathVariable(value = "userId") @Parameter(description = "ID of the user to upgrade") String userId,
            @RequestParam @Parameter(description = "New role for the user", example = "ROLE_AGENT") String newRole,
            Authentication authentication) {

        return userService.setUserAccess(userId, newRole)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
