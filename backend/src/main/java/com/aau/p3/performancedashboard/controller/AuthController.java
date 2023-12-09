package com.aau.p3.performancedashboard.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.dto.CustomResponse;
import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.LoginRequest;
import com.aau.p3.performancedashboard.payload.request.SignupRequest;
import com.aau.p3.performancedashboard.payload.response.ErrorResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.jwt.JwtUtils;
import com.aau.p3.performancedashboard.security.services.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Authentication Management APIs")
@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final Logger logger = LogManager.getLogger(AuthController.class);

  // Dependencies
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;

  // Constructor injection
  @Autowired
  public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                        RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
      this.authenticationManager = authenticationManager;
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
      this.encoder = encoder;
      this.jwtUtils = jwtUtils;
  }

  

  @Operation(summary = "Authenticate a user", description = "The request body must include a username and a password.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully authenticated user"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @PostMapping(path = "/signin", consumes = "application/json", produces = "application/json")
  public Mono<ResponseEntity<String>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    logger.error("Authentication: " + authentication.toString()); 

    SecurityContextHolder.getContext().setAuthentication(authentication);
    logger.error("Security context: " + SecurityContextHolder.getContext().toString());

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    logger.error("User details: " + userDetails.toString());

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
    logger.error("JWT cookie: " + jwtCookie.toString());

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    logger.error("Roles: " + roles.toString());

    logger.error("Successfully authenticated user");
    return Mono.just(ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body("Successfully authenticated user"));
  }

  @Operation(summary = "Register a new user", description = "The request body must include a username, an email, a password and optionally a list of roles. If roles are not provided, agent role will be assigned by default.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully registered user"),
      @ApiResponse(responseCode = "400", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @PostMapping("/signup")
  public Mono<ResponseEntity<MessageResponse>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    // Check if username or email already exists
    if (userRepository.existsByUsername(signUpRequest.getUsername()).block()) {
      return Mono.just(ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!")));
    }

    // Check if email already exists
    if (userRepository.existsByEmail(signUpRequest.getEmail()).block()) {
      return Mono.just(ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!")));
    }

    // Create new user's account
    String id = UUID.randomUUID().toString();
    User user = new User(id, signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    // Get the roles from the request body
    List<String> strRoles = signUpRequest.getRoles();

    // If no roles are provided, assign agent role by default
    // Otherwise, assign the roles provided
    return Flux.fromIterable(strRoles == null ? Collections.singletonList("agent") : strRoles)
    .flatMap(roleName -> {
        switch (roleName) {
            case "admin":
                return roleRepository.findByName(ERole.ROLE_ADMIN);
            case "supervisor":
                return roleRepository.findByName(ERole.ROLE_SUPERVISOR);
            case "tv":
                return roleRepository.findByName(ERole.ROLE_TV);
            default:
                return roleRepository.findByName(ERole.ROLE_AGENT);
        }
    })
    .collectList()
    .flatMap(roles -> {
        user.setRoles(roles);
        return userRepository.save(user);
    })
    .map(savedUser -> ResponseEntity.ok(new MessageResponse("User registered successfully!")))
    .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()))));
  }

  @Operation(summary = "Retrieve all users", description = "The response object will contain a list of all users.")
  @GetMapping(path = "/users", produces = "application/json")
  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  @ResponseBody
  @ExceptionHandler(AuthenticationException.class)
  public Mono<ResponseEntity<CustomResponse>> handleAuthException(AuthenticationException ex) {
    CustomResponse response = new CustomResponse("Error authenticating.", "error", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
      ErrorResponse response = new ErrorResponse(ex.getMessage(), "error", "Internal Server Error");
      return ResponseEntity.internalServerError().body(response);
  }
}


