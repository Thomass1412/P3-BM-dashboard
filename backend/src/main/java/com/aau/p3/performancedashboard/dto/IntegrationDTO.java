package com.aau.p3.performancedashboard.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationDTO {
    
    private String name;
    private String type;
    private Date lastUpdated;

    IntegrationDTO(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
}
