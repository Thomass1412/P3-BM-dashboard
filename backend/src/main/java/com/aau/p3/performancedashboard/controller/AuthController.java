package com.aau.p3.performancedashboard.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.LoginRequest;
import com.aau.p3.performancedashboard.payload.request.SignupRequest;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.jwt.JwtUtils;
import com.aau.p3.performancedashboard.security.services.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name ="Authentication", description = "Authentication Management APIs")
@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

      @Operation(
        summary = "Authenticate a user",
        description = "The request body must include a username and a password.")
      @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully authenticated user"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
      })
  @PostMapping(path = "/signin", consumes = "application/json", produces = "application/json")
  public ResponseEntity<MessageResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        if (!authentication.isAuthenticated()) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Authentication failed."));
        }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return Mono.just(ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new MessageResponse("Successfully authenticated user")));
  }

      @Operation(
        summary = "Register a new user",
        description = "The request body must include a username, an email, a password and optionally a list of roles. If roles are not provided, agent role will be assigned by default.")
      @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully registered user"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
      })
  @PostMapping("/signup")
  public Mono<ResponseEntity<?>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return Mono.just(ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!")));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return Mono.just(ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!")));
    }

    // Create new user's account
    String id = UUID.randomUUID().toString();
    User user = new User(id, signUpRequest.getUsername(), 
                         signUpRequest.getEmail(),
                         encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role agentRole = roleRepository.findByName(ERole.ROLE_AGENT)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(agentRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "supervisor":
          Role supervisorRole = roleRepository.findByName(ERole.ROLE_SUPERVISOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(supervisorRole);

          break;
        case "tv":
          Role tvRole = roleRepository.findByName(ERole.ROLE_TV)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(tvRole);

          break;
        default:
          Role agentRole = roleRepository.findByName(ERole.ROLE_AGENT)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(agentRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return Mono.just(ResponseEntity.ok(new MessageResponse("User registered successfully!")));    
  }

  @Operation(
    summary = "Retrieve all users",
    description = "The response object will contain a list of all users.")
  @GetMapping(path="/users", produces = "application/json")
  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  @ResponseBody
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<CustomResponse> handleUnfoundRequest(AuthenticationException ex) {
      CustomResponse response = new CustomResponse("Error authenticating.", "error", ex.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

}
