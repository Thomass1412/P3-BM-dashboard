package com.aau.p3.performancedashboard;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories(basePackages = "com.aau.p3.performancedashboard.repository")
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
@ComponentScan(basePackages = {
	"com.aau.p3.performancedashboard.repository",
	"com.aau.p3.performancedashboard.config",
	"com.aau.p3.performancedashboard.security",
	"com.aau.p3.performancedashboard.controller",
	"com.aau.p3.performancedashboard.service",
	"com.aau.p3.performancedashboard.converter",
	"com.aau.p3.performancedashboard"
})
public class PerformancedashboardApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PerformancedashboardApplication.class, args);

		// Print the names of all beans in the application context
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
	}

}