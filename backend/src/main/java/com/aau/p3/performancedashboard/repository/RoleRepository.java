package com.aau.p3.performancedashboard.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
