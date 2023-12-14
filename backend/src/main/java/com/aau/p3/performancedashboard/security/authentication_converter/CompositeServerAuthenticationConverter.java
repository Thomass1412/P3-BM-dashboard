package com.aau.p3.performancedashboard.security.authentication_converter;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CompositeServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final List<ServerAuthenticationConverter> converters;

    public CompositeServerAuthenticationConverter(List<ServerAuthenticationConverter> converters) {
        this.converters = converters;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Flux.fromIterable(converters)
                .flatMap(converter -> converter.convert(exchange))
                .next();
    }
}