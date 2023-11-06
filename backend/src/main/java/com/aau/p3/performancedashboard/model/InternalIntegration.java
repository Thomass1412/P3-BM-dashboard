package com.aau.p3.performancedashboard.model;

import java.util.Date;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;

@Document
public class InternalIntegration {

  @Id
  private String id;

  private String title;

  private Map<Date, Integer> data;

  private int datapoint;

  public InternalIntegration(String id, String title, Map<Date, Integer> data) {
    this.id = id;
    this.title = title;
    this.data = data;
  }

  public InternalIntegration(String id, String title, Map<Date, Integer> data, int datapoint) {
    this.id = id;
    this.title = title;
    this.data = data;
    this.datapoint = datapoint;
  }

  public InternalIntegration(String id, int datapoint) {
    this.id = id;
    this.datapoint = datapoint;
  }

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

  // append data
  public void appendData(Integer key) {
    this.data.put(new Date(), key);
  }

  public Map<Date, Integer> getData() {
    return this.data;
  }

  public void setData(Map<Date, Integer> data) {
    this.data = data;
  }

  public int getDatapoint() {
    return this.datapoint;
  }

  public void setDatapoint(int datapoint) {
    this.datapoint = datapoint;
  }

  @Override
  public String toString() {
    return "InternalIntegration [id=" + id + ", title=" + title + ", data=" + data + "]";
  }
}
