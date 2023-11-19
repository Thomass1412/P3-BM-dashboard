package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@ToString
@NoArgsConstructor
@TypeAlias("integration")
@Document(collection = "integration")
public class Integration {

    @Id
    @Schema(name = "Integration ID (ObjectId)", example = "655a40f451a6f25c2b736e30", required = true)
    @Getter private String id;

    @Getter
    @Setter
    @NotBlank(message = "Name must not be empty")
    @Schema(name = "Integration name", example = "My Sales Integration", required = true)
    @Size(min = 5, max = 50)
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "Type must not be empty")
    @Schema(name = "Integration type", example = "internal", required = true)
    private String type;

    @Getter @Setter private Date lastUpdated;
    
    @DBRef
    private List<IntegrationData> integrationData;

    public Integration(String name) {
        this.name = name;
    }

    public Integration(String name, String type) {
        this.name = name;
        this.type = type;

    }
}
