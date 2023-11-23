package com.aau.p3.performancedashboard;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.aau.p3.performancedashboard.repository.UserRepository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SetupSteps {

	Logger logger = LoggerManager.getLogger(SetupSteps.class);

	@Autowired
private Environment environment;

	public void setup() {
		// put functions in here, that spring should execute on startup.
		// this could be something that should be run on each startup, or
		// something that should be run only once, during the first startup.
		// functions themselfs are responsible for checking if they should be run or not.

		this.createAdminUser();
	}

	  @Autowired
  UserRepository userRepository;

	private void createAdminUser() {
		// check if admin user exists
		// if not, create it
		// if yes, do nothing

		// check if admin user exists
		if (userRepository.existsByUsername("admin")) {
			// if yes, do nothing
			return;
		}
	}
}

