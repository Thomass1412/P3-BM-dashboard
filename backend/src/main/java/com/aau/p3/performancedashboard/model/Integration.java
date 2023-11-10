package com.aau.p3.performancedashboard.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@TypeAlias("integration")
@Document(collection = "integration")
public class Integration {

    @Id
    private String id;

    @Getter @Setter private String name;
    @Getter @Setter private String type;
    @Getter @Setter private Date lastUpdated;

    Integration(String name, String type) {
        this.name = name;
        this.type = type;

    }

    @Override
    public String toString() {
      return "Integration [id=" + this.id + ", name=" + this.name +"]";
    }
}
