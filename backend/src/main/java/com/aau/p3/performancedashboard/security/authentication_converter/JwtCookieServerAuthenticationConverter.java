package com.aau.p3.performancedashboard.security.authentication_converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import com.aau.p3.performancedashboard.security.TokenProvider;

import reactor.core.publisher.Mono;

public class JwtCookieServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final TokenProvider tokenProvider;
    private final String jwtCookieName;

    public JwtCookieServerAuthenticationConverter(TokenProvider tokenProvider, @Value("${performancedashboard.app.jwtCookieName}") String jwtCookieName) {
        this.tokenProvider = tokenProvider;
        this.jwtCookieName = jwtCookieName;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst(jwtCookieName))
                .filter(cookie -> tokenProvider.validateToken(cookie.getValue()))
                .map(cookie -> new UsernamePasswordAuthenticationToken(tokenProvider.getAuthentication(cookie.getValue()), null));
    }
}
