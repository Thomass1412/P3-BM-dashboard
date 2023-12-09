package com.aau.p3.performancedashboard.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;
@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}