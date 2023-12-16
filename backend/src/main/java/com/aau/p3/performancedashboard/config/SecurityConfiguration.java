package com.aau.p3.performancedashboard.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.security.AuthorityConstant;
import com.aau.p3.performancedashboard.security.TokenProvider;
import com.aau.p3.performancedashboard.security.authentication_converter.CompositeServerAuthenticationConverter;
import com.aau.p3.performancedashboard.security.authentication_converter.JwtBearerServerAuthenticationConverter;
import com.aau.p3.performancedashboard.security.authentication_converter.JwtCookieServerAuthenticationConverter;
import com.aau.p3.performancedashboard.security.jwt.JWTHeaderExhanger;
import com.aau.p3.performancedashboard.security.jwt.JWTReactiveAuthenticationManager;
import com.aau.p3.performancedashboard.service.ReactiveUserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfiguration {

    // Whitelist swagger and endpoints
    private static String[] AUTH_WHITELIST;

    // Cookie
    @Value("${performancedashboard.app.jwtCookieName}")
    private String jwtCookieName;

    // Autowired dependencies
    private final ReactiveUserDetailsServiceImpl reactiveUserDetailsService;
    private final TokenProvider tokenProvider;
    @SuppressWarnings("unused")
    private final Environment environment;

    // Constructor with autowired dependencies
    public SecurityConfiguration(ReactiveUserDetailsServiceImpl reactiveUserDetailsService, TokenProvider tokenProvider,
            Environment environment) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.tokenProvider = tokenProvider;
        this.environment = environment;

        // Check if authentication is disabled via environment variable
        if (Objects.equals(environment.getProperty("disable.auth"), "true")) {
            // Disable authentication
            AUTH_WHITELIST = new String[] {
                    "/**"
            };
        } else {
            AUTH_WHITELIST = new String[] {
                    "/api/v1/**",
                    "/api/v1/docs/**",
                    "/auth/**"
            };
        }
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
     * Configures the security filter chain for the application.
     *
     * @param httpSecurity the ServerHttpSecurity object used to configure security settings
     * @return the configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint((swe, e) -> {
                        // By handling the entrypoint we can disable prompt for login
                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        swe.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                        // Create and write response
                        MessageResponse messageResponse = new MessageResponse(e.getMessage());
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json;
                        try {
                            json = objectMapper.writeValueAsString(messageResponse);
                        } catch (JsonProcessingException ex) {
                            json = "{\"message\":\"An error occurred while processing the request.\"}";
                        }
                        DataBuffer buffer = swe.getResponse().bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
                        return swe.getResponse().writeAndFlushWith(Mono.just(Flux.just(buffer)));
                    }))
                .authorizeExchange(authorize -> authorize
                        .matchers(EndpointRequest.to("health", "info")).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .matchers(EndpointRequest.toAnyEndpoint()).hasAuthority(AuthorityConstant.ADMIN)
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(webFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    /**
     * Creates an instance of AuthenticationWebFilter.
     * 
     * @return the created AuthenticationWebFilter instance
     */
    @Bean
    public AuthenticationWebFilter webFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(
                repositoryReactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        authenticationWebFilter.setRequiresAuthenticationMatcher(new JWTHeaderExhanger());
        authenticationWebFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        return authenticationWebFilter;
    }

    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        List<ServerAuthenticationConverter> converters = new ArrayList<>();
        converters.add(new JwtBearerServerAuthenticationConverter(tokenProvider));
        converters.add(new JwtCookieServerAuthenticationConverter(tokenProvider, jwtCookieName));
        return new CompositeServerAuthenticationConverter(converters);
    }

    /**
     * Creates a JWTReactiveAuthenticationManager instance.
     * 
     * @param reactiveUserDetailsService the reactive user details service
     * @param passwordEncoder            the password encoder
     * @return the JWTReactiveAuthenticationManager instance
     */
    @Bean
    public JWTReactiveAuthenticationManager repositoryReactiveAuthenticationManager() {
        JWTReactiveAuthenticationManager repositoryReactiveAuthenticationManager = new JWTReactiveAuthenticationManager(
                reactiveUserDetailsService, passwordEncoder());
        return repositoryReactiveAuthenticationManager;
    }
}