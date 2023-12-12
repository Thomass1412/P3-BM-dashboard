package com.aau.p3.performancedashboard;

import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;

import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;

import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
@Setter
public class Setup {

	private final Logger logger = LoggerFactory.getLogger(Setup.class);

	private Environment environment;
    private PasswordEncoder encoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public Setup() {
        // Default constructor
    }

    @Autowired
    public Setup(Environment environment, PasswordEncoder encoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.environment = environment;
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

	public void run() {
		logger.info("Processing setup steps");

		createRoles();

		//createAdminUser();

		logger.info("Finished processing setup steps");
	}

	/*
	   ROLE_TV,
  ROLE_AGENT,
  ROLE_SUPERVISOR,
  ROLE_ADMIN
	 */

	private void createRoles() {
		// Only create, if not found
		if (!roleRepository.findByName(ERole.ROLE_AGENT).hasElement().block()) {
			Role agentRole = new Role(ERole.ROLE_AGENT);
			roleRepository.save(agentRole).block();
		} else if(!roleRepository.findByName(ERole.ROLE_SUPERVISOR).hasElement().block()) {
			Role supervisorRole = new Role(ERole.ROLE_SUPERVISOR);
			roleRepository.save(supervisorRole).block();
		} else if(!roleRepository.findByName(ERole.ROLE_ADMIN).hasElement().block()) {
			Role userRole = new Role(ERole.ROLE_ADMIN);
			roleRepository.save(userRole).block();
		} else if(!roleRepository.findByName(ERole.ROLE_TV).hasElement().block()) {
			Role userRole = new Role(ERole.ROLE_TV);
			roleRepository.save(userRole).block();
		} else {
			this.logger.info("Roles already created");
			return;
		}
	}

	private void createAdminUser() {
		// Dont create admin user if it already exists
		if (userRepository.existsByUsername("admin").block()) {
			// if yes, do nothing
			return;
		}

		// Create admin user
		String id = UUID.randomUUID().toString();
		User user = new User(id, environment.getProperty("admin.username"), environment.getProperty("admin.email"),
				encoder.encode(environment.getProperty("admin.password")));
		List<Role> roles = new LinkedList<>();
		Role adminRole = roleRepository.findByName(ERole.ROLE_AGENT).switchIfEmpty(Mono.error(new RuntimeException("Error: Role is not found."))).block();
		roles.add(adminRole);
		user.setRoles(roles);
		userRepository.save(user);
		this.logger.info("Created admin user");

	}
}
