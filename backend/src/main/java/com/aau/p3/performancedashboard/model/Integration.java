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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@ToString
@NoArgsConstructor
@TypeAlias("integration")
@Document(collection = "integration")
public class Integration {

    @Id
    @Getter private String id;

    @Getter
    @Setter
    @NotBlank(message = "Name must not be empty")
    @Size(min = 0, max = 5)
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "Type must not be empty")
    @Size(min = 0, max = 20)
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
