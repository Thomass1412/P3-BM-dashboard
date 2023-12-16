package com.aau.p3.performancedashboard.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MongoConfig {

    // Logger
    private static final Logger logger = LogManager.getLogger(MongoConfig.class);

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
        logger.debug("Creating MongoClient bean.");
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
        logger.debug("Creating ReactiveMongoOperations bean.");
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
        logger.debug("Creating MongoDatabase bean.");
        return mongoClient.getDatabase(mongoDatabaseName);
    }
}
