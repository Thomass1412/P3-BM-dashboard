package com.aau.p3.performancedashboard.schema;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;

import reactor.core.publisher.Mono;

public class MongoSchemaRetriever {

    /**
     * Retrieves the schema for a given collection from the specified MongoDatabase.
     *
     * @param collectionName the name of the collection to retrieve the schema for
     * @param database       the MongoDatabase instance to retrieve the schema from
     * @return a Mono emitting the schema document if the collection exists, or an
     *         error if the collection does not exist
     */
    public Mono<Document> retrieveSchema(String collectionName, MongoDatabase database) {
        return Mono.from(database.listCollections().filter(byName(collectionName)).first()).flatMap(collection -> {
            if (collection == null) {
                return Mono.error(
                        new IllegalArgumentException("Collection with name '" + collectionName + "' does not exist."));
            } else {
                return Mono.just(readJsonSchema(collection));
            }
        });
    }

    /**
     * Returns a Bson filter that matches documents by the specified collection
     * name.
     *
     * @param collectionName the name of the collection to filter by
     * @return a Bson filter that matches documents by the specified collection name
     */
    private Bson byName(String collectionName) {
        return Filters.eq("name", collectionName);
    }

    /**
     * Reads the JSON schema from the given collection document.
     *
     * @param collection the collection document containing the JSON schema
     * @return the JSON schema document
     */
    private static Document readJsonSchema(Document collection) {
        Document options = collection.get("options", Document.class);
        Document validator = options.get("validator", Document.class);

        return validator.get("$jsonSchema", Document.class);
    }
}
