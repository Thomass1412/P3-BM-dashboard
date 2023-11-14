package com.aau.p3.performancedashboard.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Document
public class IntegrationData {
    @Id
    private String id;

    @Getter @Setter private Date timestamp;
    @Getter @Setter private String myText;

    IntegrationData() {
        this.timestamp = new Date();
        this.myText = "Hello World";
    }
}
