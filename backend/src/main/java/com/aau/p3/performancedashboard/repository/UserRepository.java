package com.aau.p3.performancedashboard.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Represents a repository for managing {@link User} instances.
 * Extends {@link ReactiveMongoRepository} to provide CRUD operations for {@link User} instances.
 * Annotated with {@link Repository} to indicate its role as a repository.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    /**
     * Retrieves all {@link User} instances with pagination.
     * Uses Spring Data MongoDB's derived query feature for automatic query generation.
     *
     * @param pageable The pagination information.
     * @return A {@link Flux} of {@link User} instances for the given page, or {@link Flux#empty()} if none exist.
     */
    public Flux<User> findAllBy(Pageable pageable);

    /**
     * Checks if a user exists by their login.
     *
     * @param login the login identifier of the user.
     * @return true if a user with the given login exists, false otherwise.
     */
    public Mono<Boolean> existsByLogin(String login);

    /**
     * Checks if a user exists by their id.
     *
     * @param id the unique identifier of the user.
     * @return true if a user with the given id exists, false otherwise.
     */
    public Mono<Boolean> existsById(String id);

    /**
     * Finds a {@link User} by their login.
     * Uses derived query feature for query generation.
     *
     * @param login The login of the {@link User} to find.
     * @return A {@link Mono} with the {@link User}, or {@link Mono#empty()} if not found.
     */
    public Mono<User> findByLogin(String login);

    /**
     * Finds a {@link User} by their id.
     * Uses derived query feature for query generation.
     *
     * @param id The id of the {@link User} to find.
     * @return A {@link Mono} with the {@link User}, or {@link Mono#empty()} if not found.
     */
    public Mono<User> findById(String id);
  
}
