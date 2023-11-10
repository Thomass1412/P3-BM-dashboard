package com.aau.p3.performancedashboard.http.Request;

import java.util.HashMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InternalIntegrationInsertDataRequest {
  private HashMap<String, Long> data;

  // getters and setters
  public HashMap<String, Long> getData() {
    return this.data;
  }

  public void setData(HashMap<String, Long> data) {
    this.data = data;
  }
}



    
