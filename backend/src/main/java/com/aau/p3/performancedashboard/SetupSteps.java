package com.aau.p3.performancedashboard;

import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.p3.performancedashboard.model.ERole;
import com.aau.p3.performancedashboard.model.Role;
import com.aau.p3.performancedashboard.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.aau.p3.performancedashboard.repository.RoleRepository;
import com.aau.p3.performancedashboard.repository.UserRepository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SetupSteps {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
private Environment environment;

	public void setup() {
		// put functions in here, that spring should execute on startup.
		// this could be something that should be run on each startup, or
		// something that should be run only once, during the first startup.
		// functions themselfs are responsible for checking if they should be run or not.

		this.logger.info("Processing setup steps");

		this.createAdminUser();

		this.logger.info("Finished processing setup steps");
	}

	  @Autowired
  PasswordEncoder encoder;

	  @Autowired
  UserRepository userRepository;

    @Autowired
  RoleRepository roleRepository;

	private void createAdminUser() {
		// check if admin user exists
		// if not, create it
		// if yes, do nothing

		// check if admin user exists
		if (userRepository.existsByUsername("admin")) {
			// if yes, do nothing
			return;
		}

		// if not, create it
    // Create new user's account
    String id = UUID.randomUUID().toString();
    User user = new User(id, environment.getProperty("admin.username"), environment.getProperty("admin.email"), encoder.encode(environment.getProperty("admin.password")));
	Set<Role> roles = new HashSet<>();
	Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
			.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	roles.add(adminRole);
    user.setRoles(roles);
    userRepository.save(user);
	this.logger.info("Created admin user");
		

		
	}
}


