package com.aau.p3.performancedashboard.integration;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "integration")
public abstract class Integration {
    @Id
    private String id;
    private String name;

    Integration(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void initialize() {

    }

    @Override
    public String toString() {
      return "InternalIntegration [id=" + this.id + ", name=" + this.name +"]";
    }
}
