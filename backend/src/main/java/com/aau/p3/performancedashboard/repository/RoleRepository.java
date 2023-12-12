package com.aau.p3.performancedashboard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;

import reactor.core.publisher.Mono;

/**
 * Repository interface for {@link Role} instances. Provides basic CRUD operations due to the extension of
 * {@link ReactiveMongoRepository}. Includes custom implemented functionality by method signatures.
 */
@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {
  
  /**
   * Finds a {@link Role} by their name.
   *
   * @param name the name of the Role
   * @return an Optional containing the Role if found, or empty if not
   */
  Mono<Role> findByName(ERole name);

  /**
   * Retrieves a role by its ID.
   *
   * @param id the ID of the role to retrieve
   * @return a Mono emitting the role with the specified ID, or an empty Mono if not found
   */
  Mono<Role> findById(String id);
}
