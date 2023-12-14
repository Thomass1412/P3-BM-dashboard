package com.aau.p3.performancedashboard.security.jwt;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for authenticating the provided authentication object using JWT (JSON Web Token).
 * If the authentication object is already authenticated, it returns the same object.
 * Otherwise, it performs the necessary authentication steps and returns a new authentication object.
 */
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    // Logger
    private final Logger logger = LogManager.getLogger(this.getClass());

    // Dependencies
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // Constructor
    public JWTReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates the provided authentication object.
     * If the authentication object is already authenticated, it returns the same object.
     * Otherwise, it performs the necessary authentication steps and returns a new authentication object.
     *
     * @param authentication The authentication object to be authenticated.
     * @return A Mono containing the authenticated authentication object.
     */
    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return Mono.just(authentication)
                .switchIfEmpty(Mono.defer(this::raiseBadCredentials))
                .cast(UsernamePasswordAuthenticationToken.class)
                .flatMap(this::authenticateToken)
                .publishOn(Schedulers.parallel())
                .onErrorResume(e -> raiseBadCredentials())
                .filter(u -> passwordEncoder.matches((String) authentication.getCredentials(), u.getPassword()))
                .switchIfEmpty(Mono.defer(this::raiseBadCredentials))
                .map(u -> new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), u.getAuthorities()));
    }

    /**
     * Raises a BadCredentialsException with the message "Invalid Credentials".
     * 
     * @param <T> the type of the Mono to be returned
     * @return a Mono that emits a BadCredentialsException
     */
    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Invalid Credentials"));
    }

    /**
     * Authenticates the provided authentication token.
     * 
     * @param authenticationToken The authentication token to be authenticated.
     * @return A Mono containing the UserDetails if authentication is successful, or null otherwise.
     */
    private Mono<UserDetails> authenticateToken(final UsernamePasswordAuthenticationToken authenticationToken) {
        String username = authenticationToken.getName();
    
        logger.info("Checking authentication for user " + username);
    
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Authenticated user " + username + ", setting security context");
            return this.userDetailsService.findByUsername(username);
        }
    
        return Mono.empty();
    }
}
