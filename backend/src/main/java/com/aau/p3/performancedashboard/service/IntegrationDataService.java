package com.aau.p3.performancedashboard.service;

import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import lombok.Getter;

import org.bson.Document;
import reactor.core.publisher.Mono;

@Service
public class IntegrationDataService {

        @Getter
        public static ReactiveMongoOperations reactiveMongoOperations = new ReactiveMongoTemplate(
                        MongoClients.create("mongodb://root:secret@mongodb:27017"), "dashboard-db");

        private static final MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://root:secret@mongodb:27017"))
                        .build());

        public Mono<MongoCollection<Document>> createCollection(String collectionName, String schema) {
                MongoDatabase database = mongoClient.getDatabase("dashboard-db");

                Document doc = Document.parse(schema);
                ValidationOptions validationOptions = new ValidationOptions().validator(doc)
                                .validationAction(ValidationAction.ERROR).validationLevel(ValidationLevel.STRICT);
                                
                CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                                .validationOptions(validationOptions);

                return Mono.from(database.createCollection(collectionName, createCollectionOptions))
                                .then(Mono.just(database.getCollection(collectionName)));
        }
}
