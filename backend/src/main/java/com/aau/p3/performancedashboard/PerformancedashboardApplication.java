package com.aau.p3.performancedashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
@ComponentScan({"com.aau.p3.performancedashboard.repository", "com.aau.p3.performancedashboard.config", "com.aau.p3.performancedashboard.security", "com.aau.p3.performancedashboard.controller"})
public class PerformancedashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformancedashboardApplication.class, args);
	}

}