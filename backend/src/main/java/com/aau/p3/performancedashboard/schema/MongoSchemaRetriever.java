package com.aau.p3.performancedashboard.schema;


import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;

public class MongoSchemaRetriever {

    public Document retrieveSchema(String collectionName, MongoDatabase database) {
        Document collection = Mono.from(database.listCollections().filter(byName(collectionName)).first()).block();
        return readJsonSchema(collection);
    }

    private Bson byName(String collectionName) {
        return Filters.eq("name", collectionName);
    }

    private static Document readJsonSchema(Document collection) {
        Document options = collection.get("options", Document.class);
        Document validator = options.get("validator", Document.class);
        return validator.get("$jsonSchema", Document.class);
    }
}
