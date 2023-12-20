package com.aau.p3.performancedashboard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.converter.UserConverter;
import com.aau.p3.performancedashboard.exceptions.NotFoundException;
import com.aau.p3.performancedashboard.model.Authority;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.request.RegisterRequest;
import com.aau.p3.performancedashboard.payload.response.UserResponse;
import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.security.AuthorityConstant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.aau.p3.performancedashboard.repository.AuthorityRepository;


@Service
public class UserService {

    // Logger
    private static final Logger logger = LogManager.getLogger(UserService.class);

    // Dependencies
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final AuthorityRepository authorityRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    // Constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserConverter userConverter,
            AuthorityRepository authorityRepository, ReactiveMongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
        this.authorityRepository = authorityRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Page<UserResponse>> findAllBy(Pageable pageable) {
        return userRepository.findAllBy(pageable)
                .flatMap(user -> Mono.just(userConverter.convertToResponse(user)))
                .collectList()
                .zipWith(userRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    public Mono<UserResponse> register(RegisterRequest registerRequest) {
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
                .map(userConverter::convertToResponse);
    }

    public Mono<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public Mono<UserResponse> getUserById(String userId, Authentication authentication) {
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

    public Mono<UserResponse> getUserIdByLogin(String username, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUsername = userDetails.getUsername();

        return userRepository.findByLogin(username)
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

    public Mono<Void> deleteUserById(String userId) {
        return userRepository.findById(userId)
                .flatMap(user -> userRepository.delete(user))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found.")));
    }

    public Mono<UserResponse> setUserAccess(String userId, String newRole) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    Set<Authority> authorities = new HashSet<>();
                    Authority newAuthority = new Authority();
                    newAuthority.setName(newRole);
                    authorities.add(newAuthority);
                    user.setAuthorities(authorities);
                    return userRepository.save(user);
                })
                .map(userConverter::convertToResponse)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found.")));
    }
}
