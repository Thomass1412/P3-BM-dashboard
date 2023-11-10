package com.aau.p3.performancedashboard.model;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@TypeAlias("internalintegration")
@Document(collection = "integration")
public class InternalIntegration extends Integration{
    
    @Getter @Setter private String myField1;

    public InternalIntegration(String name, String myField1) {
        super(name, "Internal");
        this.myField1 = myField1;
    }

}
