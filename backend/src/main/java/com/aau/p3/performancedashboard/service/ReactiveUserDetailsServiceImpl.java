package com.aau.p3.performancedashboard.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.repository.UserRepository;
import com.aau.p3.performancedashboard.model.User;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class implements the ReactiveUserDetailsService interface and provides
 * the functionality to retrieve user details for authentication and authorization.
 * It interacts with the UserRepository to fetch user information and creates
 * Spring Security User objects based on the retrieved data.
 */
@Component
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    // Logger
    private static final Logger logger = LogManager.getLogger(ReactiveUserDetailsServiceImpl.class);
    
    // Dependencies
    private final UserRepository userRepository;

    // Constructor injection
    @Autowired
    public ReactiveUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds a user by their username.
     *
     * @param login the username of the user to find
     * @return a Mono emitting the UserDetails of the found user
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public Mono<UserDetails> findByUsername(String login) {
        return userRepository.findByLogin(login)
            .filter(Objects::nonNull)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found with username: " + login)))
            .doOnSuccess( user -> {
                logger.debug("User found: {}", user);
            })
            .map(this::createSpringSecurityUser);
    }

    /**
     * Creates a Spring Security User object based on the provided User object.
     *
     * @param user the User object containing the user details
     * @return a Spring Security User object
     */
    public org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getLogin(),
            user.getPassword(),
            grantedAuthorities
        );
    }
}