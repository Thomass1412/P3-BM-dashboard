package com.aau.p3.performancedashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.username}")
    private String mongoUsername;

    @Value("${spring.data.mongodb.password}")
    private String mongoPassword;

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabaseName;

    /**
     * Creates a MongoClient bean.
     *
     * @return MongoClient instance with the configured MongoDB URI.
     */
    @Bean
    public MongoClient mongoClient() {
        String mongoUri = String.format("mongodb://%s:%s@%s:%s", mongoUsername, mongoPassword, mongoHost, mongoPort);
        return MongoClients.create(mongoUri);
    }

    /**
     * Creates a ReactiveMongoOperations bean.
     *
     * @param mongoClient MongoClient instance.
     * @return ReactiveMongoOperations instance with the configured MongoClient and database name.
     */
    @Bean("reactiveMongoTemplate")
    public ReactiveMongoOperations reactiveMongoOperations(MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, mongoDatabaseName);
    }

    /**
     * Creates a MongoDatabase bean.
     *
     * @param mongoClient MongoClient instance.
     * @return MongoDatabase instance with the configured MongoClient and database name.
     */
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(mongoDatabaseName);
    }
}
