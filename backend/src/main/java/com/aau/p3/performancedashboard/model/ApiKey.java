package com.aau.p3.performancedashboard.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "api-keys")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Data
public class ApiKey {
    private String key;
    private String name;

    @DocumentReference(lazy = true)
    private User creator;

    private Date creationDate;
    private Date expirationDate;
}
