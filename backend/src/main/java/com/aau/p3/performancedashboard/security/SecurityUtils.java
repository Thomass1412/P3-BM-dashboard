package com.aau.p3.performancedashboard.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.server.ServerWebExchange;

import org.springframework.security.core.userdetails.User;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@NoArgsConstructor
public final class SecurityUtils {
    /**
     * Retrieves the token from the request's authorization header.
     *
     * @param serverWebExchange the server web exchange object representing the HTTP request and response
     * @return the token extracted from the authorization header, or an empty string if no token is found
     */
    public static String getTokenFromRequest(ServerWebExchange serverWebExchange) {
        String token = serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(token) ? token : Strings.EMPTY;
    }

    /**
     * Retrieves the username of the authenticated user from the given ServerWebExchange.
     *
     * @param serverWebExchange the ServerWebExchange object representing the HTTP request and response
     * @return a Mono emitting the username of the authenticated user
     */
    public static Mono<String> getUserFromRequest(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getPrincipal()
                .cast(UsernamePasswordAuthenticationToken.class)
                .map(UsernamePasswordAuthenticationToken::getPrincipal)
                .cast(User.class)
                .map(User::getUsername);
    }
}
