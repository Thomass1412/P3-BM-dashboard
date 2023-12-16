package com.aau.p3.performancedashboard.repository;
import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.Authority;

import reactor.core.publisher.Mono;

@Component
public interface CustomAuthorityRepository {
    Mono<Authority> findById(String name);
    Mono<Authority> findByName(String name);
    Mono<Boolean> existsByName(String name);
}
