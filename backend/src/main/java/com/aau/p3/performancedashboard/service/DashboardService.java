package com.aau.p3.performancedashboard.service;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aau.p3.performancedashboard.repository.DashboardRepository;
import com.aau.p3.performancedashboard.repository.IntegrationRepository;
import com.aau.p3.performancedashboard.repository.MetricRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.aau.p3.performancedashboard.converter.DashboardConverter;
import com.aau.p3.performancedashboard.model.Dashboard;
import com.aau.p3.performancedashboard.payload.request.CreateDashboardRequest;
import com.aau.p3.performancedashboard.payload.response.DashboardResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class DashboardService {

    // Logger
    private static final Logger logger = LogManager.getLogger(DashboardService.class);

    // Dependencies
    private final DashboardRepository dashboardRepository;
    private final MetricRepository metricRepository;
    private final IntegrationRepository integrationRepository;
    private final DashboardConverter dashboardConverter;

    // Constructor injection
    public DashboardService(DashboardRepository dashboardRepository, DashboardConverter dashboardConverter,
            MetricRepository metricRepository, IntegrationRepository integrationRepository) {
        this.dashboardRepository = dashboardRepository;
        this.dashboardConverter = dashboardConverter;
        this.metricRepository = metricRepository;
        this.integrationRepository = integrationRepository;
    }

    // In-memory list of dashboards
    @SuppressWarnings("unused")
    private static List<Dashboard> dashboards;

    // In-memory map of which user is using which dashboard
    @SuppressWarnings("unused")
    private static Map<String, Dashboard> userDashboards = new HashMap<>();

    // Load the dashboards on startup
    @EventListener(ContextRefreshedEvent.class)
    public void loadDashboards() {
        logger.info("Loading dashboards...");
        List<Dashboard> dashboards = dashboardRepository.findAll().collectList().block();

        if (dashboards != null) {
            logger.info("Loaded " + dashboards.size() + " dashboards.");
            DashboardService.dashboards = dashboards;
        } else {
            logger.info("No dashboards found.");
        }
    }

    public Mono<DashboardResponse> createDashboard(CreateDashboardRequest createDashboardRequest) {
        logger.debug("Creating dashboard with name: " + createDashboardRequest.getName());

        Dashboard dashboard = dashboardConverter.convertToEntity(createDashboardRequest);

        return dashboardRepository.existsByName(createDashboardRequest.getName())
                .flatMap(dashboardExists -> {
                    if (dashboardExists) {
                        logger.error("Dashboard with name " + createDashboardRequest.getName() + " already exists.");
                        return Mono.error(new IllegalArgumentException(
                                "Dashboard with name " + createDashboardRequest.getName() + " already exists."));
                    } else {
                        logger.debug("Dashboard with name " + createDashboardRequest.getName()
                                + " does not exist. Proceeding to create.");
                                
                        // Extract all widget option IDs
                        List<String> metricIds = createDashboardRequest.getWidgets().stream()
                                .map(widget -> widget.getOptions().getMetricId())
                                .distinct()
                                .collect(Collectors.toList());

                        List<String> integrationIds = createDashboardRequest.getWidgets().stream()
                                .map(widget -> widget.getOptions().getIntegrationId())
                                .distinct()
                                .collect(Collectors.toList());

                        // Check if metrics and integrations exist
                        return Flux.fromIterable(metricIds)
                                .flatMap(metricId -> metricRepository.existsById(metricId)
                                        .flatMap(exists -> exists ? Mono.empty() : Mono.error(new IllegalArgumentException("Metric ID " + metricId + " does not exist"))))
                                .thenMany(Flux.fromIterable(integrationIds))
                                .flatMap(integrationId -> integrationRepository.existsById(integrationId)
                                        .flatMap(exists -> exists ? Mono.empty() : Mono.error(new IllegalArgumentException("Integration ID " + integrationId + " does not exist"))))
                                .then(dashboardRepository.save(dashboard))
                                .flatMap(savedDashboard -> {
                                    logger.debug("Created dashboard with name: " + savedDashboard.getName());
                                    return Mono.just(dashboardConverter.convertToResponse(savedDashboard));
                                });
                    }
                });
    }

        public Mono<Page<Dashboard>> findAllBy(Pageable pageable) {
        return dashboardRepository.findAllBy(pageable)
                .collectList()
                .zipWith(metricRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    public Mono<MessageResponse> deleteDashboard(String dashboardId) {
        logger.debug("Deleting dashboard with id: " + dashboardId);

        return dashboardRepository.existsById(dashboardId)
                .flatMap(dashboardExists -> {
                    if (dashboardExists) {
                        logger.debug("Dashboard with id " + dashboardId + " exists. Proceeding to delete.");
                        return dashboardRepository.deleteById(dashboardId)
                                .then(Mono.just(new MessageResponse("Dashboard deleted successfully!")));
                    } else {
                        logger.error("Dashboard with id " + dashboardId + " does not exist.");
                        return Mono.error(new IllegalArgumentException("Dashboard with id " + dashboardId + " does not exist."));
                    }
                });
    }
}
