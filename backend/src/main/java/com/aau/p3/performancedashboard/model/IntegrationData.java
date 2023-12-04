package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Document
public class IntegrationData {

    @Id
    private ObjectId id;

    //@Getter
    //@Setter
    //private Date timestamp;

    //@Getter
    //@Setter
    //private String employee;

    @Getter
    @Setter
    Object data;

    public IntegrationData() {
        //this.timestamp = new Date();
        this.id = new ObjectId();
    }
}
