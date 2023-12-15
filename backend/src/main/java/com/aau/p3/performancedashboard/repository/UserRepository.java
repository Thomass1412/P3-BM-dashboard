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
  
  /**
   * Finds a {@link User} by their username.
   *
   * @param username the username of the User
   * @return a Mono emitting the User if found, or empty if not
   */
  Mono<User> findByUsername(String username);

  /**
   * Checks if a {@link User} exists by their username.
   *
   * @param username the username of the User
   * @return a Mono emitting true if the User exists, false otherwise
   */
  Mono<Boolean> existsByUsername(String username);

  /**
   * Checks if a {@link User} exists by their email.
   *
   * @param email the email of the User
   * @return a Mono emitting true if the User exists, false otherwise
   */
  Mono<Boolean> existsByEmail(String email);
}
