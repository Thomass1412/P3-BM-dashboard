package com.aau.p3.performancedashboard.model;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Document(collection = "integration")
public class InternalIntegration extends Integration {

    @Getter
    @Setter
    private String myField1;

    public InternalIntegration(String name, String dataCollection) {
        super(name, "internal", dataCollection);
        this.myField1 = "bob";
    }
}