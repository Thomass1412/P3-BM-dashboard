package com.aau.p3.performancedashboard.http.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalIntegrationCreationResponse {

  private String id;

  // getters and setters
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }


}
