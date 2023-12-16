package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.User;

import reactor.core.publisher.Mono;

/**
 * Repository interface for {@link User} instances. Provides basic CRUD operations due to the extension of
 * {@link ReactiveMongoRepository}. Includes custom implemented functionality by method signatures.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

  Mono<User> findByLogin(String login);
  
  Mono<Boolean> existsByLogin(String login);
}
