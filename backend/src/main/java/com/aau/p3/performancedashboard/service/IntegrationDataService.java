package com.aau.p3.performancedashboard.service;

import org.springframework.stereotype.Service;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;

import lombok.Getter;
import reactor.core.publisher.Mono;

import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Service
public class IntegrationDataService {
    @Getter
    public static ReactiveMongoOperations reactiveMongoOperations = new ReactiveMongoTemplate(
            MongoClients.create("mongodb://root:secret@mongodb:27017"), "dashboard-db");

    public String createCollection(String collectionName, String schema) {
        Document doc = Document.parse(schema);
        ValidationOptions validationOptions = new ValidationOptions();
        validationOptions.validator(doc);
        validationOptions.validationAction(ValidationAction.ERROR);
        validationOptions.validationLevel(ValidationLevel.STRICT);
        CreateCollectionOptions cco = new CreateCollectionOptions().validationOptions(validationOptions);
        Mono<MongoCollection<Document>> collection = reactiveMongoOperations.createCollection(collectionName, cco);

        return collectionName;
    }

}