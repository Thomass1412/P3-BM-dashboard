package com.aau.p3.performancedashboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Document(collection = "roles")
public class Role {
  @Getter
  @Setter
  @Id
  private String id;

  @Getter
  @Setter
  private ERole name;

  public Role(ERole name) {
    this.name = name;
  }
}
