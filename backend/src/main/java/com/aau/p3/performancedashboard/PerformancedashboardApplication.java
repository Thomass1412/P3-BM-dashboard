package com.aau.p3.performancedashboard;

import java.util.ArrayList;

import org.bson.Document;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.model.Integration;
import com.aau.p3.performancedashboard.repository.IntegrationDataRepository;
import com.aau.p3.performancedashboard.model.IntegrationData;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
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