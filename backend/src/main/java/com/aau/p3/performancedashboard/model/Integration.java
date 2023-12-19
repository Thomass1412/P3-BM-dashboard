package com.aau.p3.performancedashboard.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "integration")
@NoArgsConstructor
public class Integration {

    @Id
    @NonNull // <-- For the required args constructor
    @Schema(name = "id", example = "655a40f451a6f25c2b736e30", required = true)
    private String id;

    @NonNull
    @NotBlank(message = "Name must not be empty")
    @Schema(name = "name", example = "sales", required = true)
    @Size(min = 5, max = 50)
    private String name;
    
    @NonNull
    @NotBlank(message = "Type must not be empty")
    @Schema(name = "type", example = "internal", required = true)
    private String type;

    @Schema(name = "lastUpdated", example = "2021-05-05T12:00:00.000Z", required = true)
    private Date lastUpdated;

    @Schema(name = "dataCollection", example = "sales-db", required = true)
    private String dataCollection;

    public Integration(String name, String type, String dataCollection) {
        this.name = name;
        this.type = type;
        this.dataCollection = dataCollection;
        this.lastUpdated = new Date();
    }
}