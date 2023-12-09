package com.aau.p3.performancedashboard.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aau.p3.performancedashboard.security.jwt.AuthEntryPointJwt;
import com.aau.p3.performancedashboard.security.jwt.AuthTokenFilter;
import com.aau.p3.performancedashboard.security.services.UserDetailsServiceImpl;

// This class is responsible for telling spring how to handle authentication and authorization
@Configuration
@EnableMethodSecurity
@EnableReactiveMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  /**
   * The authentication entry point for handling unauthorized requests.
   */
  private AuthEntryPointJwt unauthorizedHandler;

  /**
   * This class represents a filter for authenticating JWT tokens.
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  /**
   * Creates and configures a DaoAuthenticationProvider.
   * 
   * @return The configured DaoAuthenticationProvider instance.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }
  
  /**
   * Returns the authentication manager.
   *
   * @param authConfig the authentication configuration
   * @return the authentication manager
   * @throws Exception if an error occurs while retrieving the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Returns a PasswordEncoder object that can be used to encode passwords.
   *
   * @return a PasswordEncoder object
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the security filter chain for the HTTP requests.
   *
   * @param http the HttpSecurity object to configure the filter chain
   * @return the configured SecurityFilterChain object
   * @throws Exception if an error occurs while configuring the filter chain
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
    .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(authorize -> authorize
    .requestMatchers("/auth/**").permitAll()

    .requestMatchers("/api/v1/swagger/**").permitAll()
    .requestMatchers("/api/v1/swagger-ui/**").permitAll()
    .requestMatchers("/api/v1/docs/**").permitAll()

    // Disabled for now
    //.requestMatchers("/api/v1/**").authenticated()
    .requestMatchers("/api/v1/**").permitAll()

    .anyRequest().permitAll()
);

        
    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}