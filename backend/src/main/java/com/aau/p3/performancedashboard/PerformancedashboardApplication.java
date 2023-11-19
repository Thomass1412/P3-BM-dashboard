package com.aau.p3.performancedashboard;



import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.aau.p3.performancedashboard.model.IntegrationData;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Performance Dashboard", version = "1.0", description = "Backend API v1.0"))
public class PerformancedashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformancedashboardApplication.class, args);

		ReactiveMongoOperations mongoOps = new ReactiveMongoTemplate(MongoClients.create("mongodb://root:secret@mongodb:27017"), "dashboard-db");

		// Create a collection
		String name = "test1";
		String collectionName = name + "-data";

		Mono<MongoCollection<Document>> coll = mongoOps.createCollection(collectionName);
		coll.subscribe(collection -> System.out.println(collection.getNamespace()));

		Mono<MongoCollection<Document>> coll2 = mongoOps.createCollection(collectionName+ "new");
		coll2.subscribe(collection -> System.out.println(collection.getNamespace()));

		// Insert a document
		Mono<IntegrationData> ie = mongoOps.save(new IntegrationData(), collectionName);
		ie.subscribe(x-> System.out.println(x));

		Mono<IntegrationData> ie2 = mongoOps.save(new IntegrationData(), collectionName+ "new");
		ie2.subscribe(x-> System.out.println(x));

		// Get all elements
		Flux<IntegrationData> allIntegrationData = mongoOps.findAll(IntegrationData.class, collectionName);
		allIntegrationData.subscribe(x -> System.out.println(x));
		
	}

}