package com.aau.p3.performancedashboard.integration;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "integration")
public class InternalIntegration extends Integration{
    
    private String myField1;

    InternalIntegration(String name, String myField1) {
        super(name);
        this.myField1 = myField1;
    }

    public String getMyField1() {
        return this.myField1;
    }

    public void setMyField1(String myField1) {
        this.myField1 = myField1;
    }

    @Override
    public String toString() {
      return "InternalIntegration [id=" + this.getId() + ", name=" + this.getName() + ", myField1=" + this.getMyField1() +"]";
    }
}
