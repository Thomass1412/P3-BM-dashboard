package com.aau.p3.performancedashboard;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@Component
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
@ComponentScan("com.aau.p3.performancedashboard.repository")
@ComponentScan("com.aau.p3.performancedashboard.security")
@ComponentScan("com.aau.p3.performancedashboard.setup")
public class PerformancedashboardApplication {

	private final Setup setup;
	private final Logger logger = LoggerFactory.getLogger(PerformancedashboardApplication.class);

	@Autowired
	public PerformancedashboardApplication(Setup setup) {
		this.setup = setup;
	}

	@PostConstruct
    public void init() {
		logger.info("Starting setup steps");
		try {
			setup.run();
		} catch (Exception e) {
			logger.error("Error during setup steps", e);
		}
    }

	public static void main(String[] args) {
		SpringApplication.run(PerformancedashboardApplication.class, args);
	}

}