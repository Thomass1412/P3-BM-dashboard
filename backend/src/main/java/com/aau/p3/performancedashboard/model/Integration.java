package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@ToString
@TypeAlias("integration")
@Document(collection = "integration")
public class Integration {

    @Id
    private String id;

    @Getter @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private Date lastUpdated;
    @DBRef
    private List<IntegrationData> integrationData;

    Integration(String name, String type) {
        this.name = name;
        this.type = type;

    }
}
