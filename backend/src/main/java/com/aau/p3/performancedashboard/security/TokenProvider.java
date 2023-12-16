package com.aau.p3.performancedashboard.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import io.jsonwebtoken.security.Keys;

/**
 * This class is responsible for generating and validating JWT tokens for authentication.
 */
@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    // Load JWT secret and expiration time from application.properties
    @Value("${performancedashboard.app.jwtSecret}")
    private String saltKey;
    
    @Value("${performancedashboard.app.jwtExpiration}")
    private int tokenValidity;

    // JWT secret and expiration time
    private static String SALT_KEY;
    private static int TOKEN_VALIDITY;

    // JWT claims
    private final String AUTHORITIES_KEY = "auth";

    private final Base64.Encoder encoder = Base64.getEncoder();

    private String secretKey;

    private long tokenValidityInMilliseconds;

    /**
        * Initializes the TokenProvider by setting the salt key, token validity, and secret key.
        * Converts the salt key to a secret key using Base64 encoding.
        * Calculates the token validity in milliseconds.
        */
    @PostConstruct
    public void init() {
        SALT_KEY = saltKey;
        TOKEN_VALIDITY = tokenValidity;
        this.secretKey = encoder.encodeToString(SALT_KEY.getBytes(StandardCharsets.UTF_8));

        this.tokenValidityInMilliseconds = 1000 * TOKEN_VALIDITY;
    }

    /**
     * Creates a token for the given authentication.
     *
     * @param authentication the authentication object
     * @return the generated token as a string
     */
    public String createToken(Authentication authentication, Optional<Date> expirationDate) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Longer expirationdate for api keys
        Date validity = null;
        if(!expirationDate.isEmpty()) {
            validity = expirationDate.get();
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .setIssuedAt(new Date())
            .compact();
    }

    /**
     * Retrieves the authentication object from the provided token.
     *
     * @param token The token used for authentication.
     * @return The authentication object.
     * @throws BadCredentialsException If the token is invalid.
     */
    public Authentication getAuthentication(String token) {
        if (!StringUtils.hasText(token) || !validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Validates a JWT token.
     *
     * @param authToken the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}
