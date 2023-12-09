package com.aau.p3.performancedashboard;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
// This is to tell spring where our repositories are located
@ComponentScan("com.aau.p3.performancedashboard.repository")
@ComponentScan("com.aau.p3.performancedashboard.security")
public class PerformancedashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformancedashboardApplication.class, args);

		// Ensures that a default admin is set up on startup as of now.
		Setup setup = new Setup();
		setup.run();
	}

}