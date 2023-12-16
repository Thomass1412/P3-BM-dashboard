package com.aau.p3.performancedashboard;

import java.util.AbstractMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.Authority;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.repository.AuthorityRepository;
import com.aau.p3.performancedashboard.security.AuthorityConstant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MongoInitializer {
    private final Logger logger = LoggerFactory.getLogger(MongoInitializer.class);

    private final Environment environment;
    private final PasswordEncoder encoder;
    private final ReactiveMongoTemplate mongoTemplate;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public MongoInitializer(Environment environment, PasswordEncoder encoder,
            ReactiveMongoTemplate mongoTemplate, AuthorityRepository authorityRepository) {
        this.environment = environment;
        this.encoder = encoder;
        this.mongoTemplate = mongoTemplate;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Handles the application event when the context is refreshed.
     * This method is triggered after the application context is **initialized** or refreshed.
     * It performs setup steps such as creating default authorities and admin.
     *
     * @param event The ContextRefreshedEvent object representing the application event.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.debug("Processing setup steps");
        createDefaultAuthorities()
                .then(createDefaultAdmin())
                .doOnSuccess(v -> logger.debug("Finished processing setup steps"))
                .doOnError(e -> logger.error("Error processing setup steps", e))
                .subscribe();
    }

    /**
     * Creates default authorities in the MongoDB collection "authorities".
     * The default authorities include ADMIN, AGENT, SUPERVISOR, and TV.
     * 
     * @return A Mono<Void> representing the completion of the operation.
     */
    private Mono<Void> createDefaultAuthorities() {

        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthorityConstant.ADMIN);

        Authority agentAuthority = new Authority();
        agentAuthority.setName(AuthorityConstant.AGENT);

        Authority supervisorAuthority = new Authority();
        supervisorAuthority.setName(AuthorityConstant.SUPERVISOR);

        Authority tvAuthority = new Authority();
        tvAuthority.setName(AuthorityConstant.TV);

        List<Authority> authorityTest = authorityRepository.findAll().collectList()
        .doOnNext(authorityList -> logger.debug("Authorities: " + authorityList.toString()))
        .switchIfEmpty(Mono.error(new IllegalArgumentException("No authorities found")))
        .doOnError(e -> logger.error("Error getting authorities", e))
        .block();

        return Flux.just(adminAuthority, agentAuthority, supervisorAuthority, tvAuthority)
            .flatMap(authority -> authorityRepository.existsByName(authority.getName())
                .map(exists -> new AbstractMap.SimpleEntry<>(authority, exists)))
            .flatMap(entry -> {
                Authority authority = entry.getKey();
                Boolean exists = entry.getValue();
                if (!exists) {
                    logger.debug("Creating default authority: " + authority.getName());
                    return mongoTemplate.save(authority, "authorities")
                        .doOnSuccess(savedAuthority -> logger.debug("Finished creating default authority: " + savedAuthority.getName()));
                } else {
                    logger.debug("Authority already exists: " + authority.getName());
                    return Mono.empty();
                }
            })
        .then();
    }

    private Mono<Void> createDefaultAdmin() {

        logger.debug("Creating default admin user");
        Authority adminAuthority = new Authority();
        adminAuthority.setName(AuthorityConstant.ADMIN);

        Authority agentAuthority = new Authority();
        agentAuthority.setName(AuthorityConstant.AGENT);

        User adminUser = new User();
        adminUser.setLogin(environment.getProperty("admin.username"));
        logger.debug("Password: " + environment.getProperty("admin.password"));
        adminUser.setPassword(encoder.encode(environment.getProperty("admin.password")));
        adminUser.setEmail(environment.getProperty("admin.email"));
        adminUser.getAuthorities().add(adminAuthority);
        adminUser.getAuthorities().add(agentAuthority);

        return mongoTemplate.exists(Query.query(Criteria.where("login").is(adminUser.getLogin())), User.class, "users")
                .flatMap(exists -> {
                    if (exists) {
                        logger.debug("Admin user already exists");
                        return Mono.empty();
                    } else {
                        return Mono.just(adminUser)
                                .doOnNext(user -> logger.debug("Creating default admin user"))
                                .flatMap(user -> mongoTemplate.save(user, "users"))
                                .doOnNext(user -> logger.debug(user.toString()))
                                .doOnSuccess(user -> logger.debug("Finished creating default admin user"))
                                .doOnError(e -> logger.error("Error creating default admin user", e));
                    }
                })
                .then();
    }
}
