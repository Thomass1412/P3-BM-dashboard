package com.aau.p3.performancedashboard;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
public class PerformancedashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformancedashboardApplication.class, args);

	}

}