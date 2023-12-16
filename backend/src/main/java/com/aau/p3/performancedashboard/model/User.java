package com.aau.p3.performancedashboard.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Data
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  private String id;

  @NotBlank
  @Size(max = 20)
  @Indexed(unique = true)
  private String login;

  @NotBlank
  @Size(max = 50)
  private String displayName;
  
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  private List<String> achievements;

  // Do not serialize the authorities
  @JsonIgnore
  private Set<Authority> authorities = new HashSet<>();
}
