package com.aau.p3.performancedashboard.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.Setter;
import lombok.ToString;
import lombok.Getter;

@ToString
@Document(collection = "users")
public class User {
  @Id
  @Getter
  @Setter
  private String id;

  @NotBlank
  @Size(max = 20)
  @Getter
  @Setter
  private String username;

  @NotBlank
  @Getter
  @Setter
  @Size(max = 50)
  @Email
  private String email;

  @Getter
  @Setter
  @NotBlank
  @Size(max = 120)
  private String password;

  @Getter
  @Setter
  private List<String> roles;

  public User() {
  }

  public User(String id, String username, String email, String password) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
  }

}
