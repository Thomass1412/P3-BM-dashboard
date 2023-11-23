package com.aau.p3.performancedashboard.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.LoginRequest;
import com.aau.p3.performancedashboard.payload.request.SignupRequest;
import com.aau.p3.performancedashboard.payload.response.UserInfoResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.jwt.JwtUtils;
import com.aau.p3.performancedashboard.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/v1/auth")
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

  @PostMapping("/signin")
  public Mono<ResponseEntity<?>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return Mono.just(ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles)));
  }

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
}
