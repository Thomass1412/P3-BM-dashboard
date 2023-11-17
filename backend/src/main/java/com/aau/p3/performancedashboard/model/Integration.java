package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.aau.p3.performancedashboard.service.IntegrationDataService;

@ToString
@NoArgsConstructor
@Document(collection = "integration")
public class Integration {

    @Id
    @Getter
    private String id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
    private Date lastUpdated;

    @Setter
    @Getter
    private String dataCollection;

    public Integration(String name, String type) {
        this.name = name;
        this.type = type;

    }

    public Mono<IntegrationData> saveIntegrationData(IntegrationData integrationData) {
        ReactiveMongoOperations reactiveMongoOperations = IntegrationDataService.getReactiveMongoOperations();
        return reactiveMongoOperations.save(integrationData, this.dataCollection);
    }
}
