package com.aau.p3.performancedashboard.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.bson.Document;

import com.aau.p3.performancedashboard.service.IntegrationDataService;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@ToString
@NoArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "integration")
public class Integration {

    @Id
    @Schema(name = "id", example = "655a40f451a6f25c2b736e30", required = true)
    @Getter private String id;

    @Getter
    @Setter
    @NotBlank(message = "Name must not be empty")
    @Schema(name = "name", example = "sales", required = true)
    @Size(min = 5, max = 50)
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "Type must not be empty")
    @Schema(name = "type", example = "internal", required = true)
    private String type;

    @Getter
    @Setter
    private Date lastUpdated;

    @Setter
    @Getter
    @Schema(name = "dataCollection", example = "sales-db", required = false)
    private String dataCollection;

    public Integration(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Mono<IntegrationData> saveIntegrationData(IntegrationData integrationData) {
        ReactiveMongoOperations reactiveMongoOperations = IntegrationDataService.getReactiveMongoOperations();
        org.bson.Document document = new Document();
        document.put("data", integrationData.getData());
        return reactiveMongoOperations.save(document, this.dataCollection)
                .map(result -> integrationData);
    }
}