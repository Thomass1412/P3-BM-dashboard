package com.aau.p3.performancedashboard.model;

import java.util.HashMap;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Document
public class InternalIntegration {

  @Id
  private String documentId;

  private String integrationId;

  private String title = "";

  private HashMap<String, Long> data = new HashMap<String, Long>();

  // getters and setters
  public String getDocumentId() {
    return this.documentId;
  }

  public void setDocumentId(String id) {
    this.documentId = id;
  }

  public String getIntegrationId() {
    return this.integrationId;
  }

  public void setIntegrationId(String integrationId) {
    this.integrationId = integrationId;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public HashMap<String, Long> getData() {
    return this.data;
  }

  public void setData(HashMap<String, Long> data) {
    this.data = data;
  }


  @Override
  public String toString() {
    return "InternalIntegration [id=" + documentId + ", title=" + title + ", data=" + data + "]";
  }
}
