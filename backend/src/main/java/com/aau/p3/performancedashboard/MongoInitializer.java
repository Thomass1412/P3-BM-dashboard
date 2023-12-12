package com.aau.p3.performancedashboard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;

import reactor.core.publisher.Mono;

@Component
public class MongoInitializer {
    private final Logger logger = LoggerFactory.getLogger(MongoInitializer.class);

    private Environment environment;
    private PasswordEncoder encoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public MongoInitializer(Environment environment, PasswordEncoder encoder,
            UserRepository userRepository, RoleRepository roleRepository) {
        this.environment = environment;
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.debug("Processing setup steps");
        createRoles();
        createAdminUser();
        logger.debug("Finished processing setup steps");
    }

    private void createRoles() {
        logger.debug("Creating roles");
        // Only create, if not found
        if (!roleRepository.findByName(ERole.ROLE_AGENT).hasElement().block()) {
            Role agentRole = roleRepository.save(new Role(ERole.ROLE_AGENT)).block();
            logger.debug("Created role: " + agentRole.getName());
        } else {
            this.logger.info("Role AGENT already created");
        }

        if (!roleRepository.findByName(ERole.ROLE_SUPERVISOR).hasElement().block()) {
            Role supervisorRole = roleRepository.save(new Role(ERole.ROLE_SUPERVISOR)).block();
            logger.debug("Created role: " + supervisorRole.getName());
        } else {
            this.logger.info("Role SUPERVISOR already created");
        }

        if (!roleRepository.findByName(ERole.ROLE_ADMIN).hasElement().block()) {
            Role userRole = roleRepository.save(new Role(ERole.ROLE_ADMIN)).block();
            logger.debug("Created role: " + userRole.getName());
        } else {
            this.logger.info("Role ADMIN already created");
        }

        if (!roleRepository.findByName(ERole.ROLE_TV).hasElement().block()) {
            Role userRole = roleRepository.save(new Role(ERole.ROLE_TV)).block();
            logger.debug("Created role: " + userRole.getName());
        } else {
            this.logger.info("Role TV already created");
        }
    }

    private void createAdminUser() {
        logger.debug("Creating admin user");
        // Dont create admin user if it already exists
        if (userRepository.existsByUsername("admin").block()) {
            logger.debug("Admin user already created. Ignoring.");
            return;
        }

        // Create admin user
        String id = UUID.randomUUID().toString();
        User user = new User(id, environment.getProperty("admin.username"), environment.getProperty("admin.email"),
                encoder.encode(environment.getProperty("admin.password")));
        List<String> roles = new LinkedList<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .switchIfEmpty(Mono.error(new RuntimeException("Error: Role is not found."))).block();
        roles.add(adminRole.getName().name()); // Save the role name as a string
        user.setRoles(roles);
        User savedUser = userRepository.save(user).block();
        logger.debug("Created admin user: " + savedUser.getUsername());
    }
}  
