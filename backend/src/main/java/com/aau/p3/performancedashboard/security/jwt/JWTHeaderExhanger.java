package com.aau.p3.performancedashboard.security.jwt;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class JWTHeaderExhanger implements ServerWebExchangeMatcher {

    /**
     * Matches the given ServerWebExchange with the JWTHeaderExhanger.
     *
     * @param exchange the ServerWebExchange to be matched
     * @return a Mono of MatchResult indicating whether the exchange matches or not
     */
    @Override
    public Mono<MatchResult> matches(final ServerWebExchange exchange) {
        Mono<ServerHttpRequest> request = Mono.just(exchange).map(ServerWebExchange::getRequest);

        // Check if the request contains an Authorization header with a Bearer token
        return request.map(ServerHttpRequest::getHeaders)
                .filter(h -> h.containsKey(HttpHeaders.AUTHORIZATION))
                .flatMap(h -> {
                    List<String> authorizationHeaders = h.get(HttpHeaders.AUTHORIZATION);
                    if (authorizationHeaders != null && authorizationHeaders.size() > 0
                            && authorizationHeaders.get(0).startsWith("Bearer ")) {
                        return MatchResult.match();
                    } else {
                        return MatchResult.notMatch();
                    }
                })
                .switchIfEmpty(MatchResult.notMatch());
    }
}