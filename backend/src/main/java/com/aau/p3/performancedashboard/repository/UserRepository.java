package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for {@link User} instances. Provides basic CRUD operations due to the extension of
 * {@link ReactiveMongoRepository}. Includes custom implemented functionality by method signatures.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

  /**
   * Finds a user by their login.
   *
   * @param login the login of the user to find
   * @return a Mono emitting the user if found, or an empty Mono if not found
   */
  public Mono<User> findByLogin(String login);

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user to retrieve
   * @return a Mono emitting the user with the specified ID, or an empty Mono if no user is found
   */
  public Mono<User> findById(String id);
  
  /**
   * Checks if a user with the specified login exists in the repository.
   *
   * @param login the login of the user to check
   * @return a Mono<Boolean> indicating whether a user with the specified login exists
   */
  public Mono<Boolean> existsByLogin(String login);

  /**
   * Retrieves all users based on the specified pageable parameters.
   *
   * @param pageable the pageable object containing the pagination information
   * @return a Flux of User objects representing the users found
   */
  public Flux<User> findAllBy(Pageable pageable);
}
