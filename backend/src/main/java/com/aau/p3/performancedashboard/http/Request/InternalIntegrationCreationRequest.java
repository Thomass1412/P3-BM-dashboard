package com.aau.p3.performancedashboard.http.Request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalIntegrationCreationRequest {

  private String id;

  private String title = "";

  // getters and setters
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "InternalIntegration [id=" + id + ", title=" + title + "]";
  }
}
