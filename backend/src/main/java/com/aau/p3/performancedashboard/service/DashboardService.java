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
import reactor.core.scheduler.Schedulers;

import com.aau.p3.performancedashboard.converter.DashboardConverter;
import com.aau.p3.performancedashboard.converter.IntegrationConverter;
import com.aau.p3.performancedashboard.converter.MetricConverter;
import com.aau.p3.performancedashboard.dashboard.DashboardWidget;
import com.aau.p3.performancedashboard.dashboard.OptionsDateIntervalCalculator;
import com.aau.p3.performancedashboard.dashboard.OptionsDateIntervalEnum;
import com.aau.p3.performancedashboard.dashboard.WidgetOptions;
import com.aau.p3.performancedashboard.model.Dashboard;
import com.aau.p3.performancedashboard.payload.request.CreateDashboardRequest;
import com.aau.p3.performancedashboard.payload.response.DashboardCalulatedResponse;
import com.aau.p3.performancedashboard.payload.response.DashboardResponse;
import com.aau.p3.performancedashboard.payload.response.MessageResponse;
import com.aau.p3.performancedashboard.payload.response.MetricResultResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final MetricService metricService;
    private final DashboardConverter dashboardConverter;
    private final MetricConverter metricConverter;
    private final OptionsDateIntervalCalculator optionsDateIntervalCalculator;

    // Constructor injection
    public DashboardService(DashboardRepository dashboardRepository, DashboardConverter dashboardConverter,
            MetricRepository metricRepository, IntegrationRepository integrationRepository, MetricService metricService,
            MetricConverter metricConverter,
            OptionsDateIntervalCalculator optionsDateIntervalCalculator) {
        this.dashboardRepository = dashboardRepository;
        this.dashboardConverter = dashboardConverter;
        this.metricRepository = metricRepository;
        this.integrationRepository = integrationRepository;
        this.metricService = metricService;
        this.metricConverter = metricConverter;
        this.optionsDateIntervalCalculator = optionsDateIntervalCalculator;
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
                                        .flatMap(exists -> exists ? Mono.empty()
                                                : Mono.error(new IllegalArgumentException(
                                                        "Metric ID " + metricId + " does not exist"))))
                                .thenMany(Flux.fromIterable(integrationIds))
                                .flatMap(integrationId -> integrationRepository.existsById(integrationId)
                                        .flatMap(exists -> exists ? Mono.empty()
                                                : Mono.error(new IllegalArgumentException(
                                                        "Integration ID " + integrationId + " does not exist"))))
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
                        return Mono.error(
                                new IllegalArgumentException("Dashboard with id " + dashboardId + " does not exist."));
                    }
                });
    }

    public Mono<DashboardCalulatedResponse> calculateDashboard(String dashboardId) {
        logger.debug("Calculating dashboard with id: " + dashboardId);

        return dashboardRepository.findById(dashboardId)
        .flatMap(dashboard -> {
            logger.debug("Dashboard with id " + dashboardId + " exists. Proceeding to calculate.");
            List<DashboardWidget> widgets = dashboard.getWidgets();

            return Flux.fromIterable(widgets)
                .parallel() // Use parallel to compute each widget calculation in parallel
                .runOn(Schedulers.parallel())
                .flatMap(this::calculateWidgetMetric)
                .sequential() // Merge results back into a sequential Flux
                .collectList()
                .map(calculatedMetrics -> new DashboardCalulatedResponse(dashboardId, dashboard.getName(), calculatedMetrics));
        });
    }

    private Mono<MetricResultResponse> calculateWidgetMetric(DashboardWidget widget) {
        // Extract options from widget
        WidgetOptions options = widget.getOptions();
        
        // Determine start and end dates
        Date[] dates = determineWidgetDates(options.getStartDateType(), options.getEndDateType(), options);
        Date startDate = dates[0];
        Date endDate = dates[1];
    
        // Calculate the metric
        return metricService.calculateMetric(
            options.getMetricId(), 
            startDate, 
            endDate, 
            options.getLimit(), 
            options.getSortedBy().keySet().iterator().next()
        );
    }

    private Date[] determineWidgetDates(OptionsDateIntervalEnum startDateInterval, OptionsDateIntervalEnum endDateInterval, WidgetOptions options) {
        Optional<Date> optionalStartDate = Optional.empty();
        Optional<Date> optionalEndDate = Optional.empty();
    
        // Check if custom dates are provided and set them if so
        if ("CUSTOM".equalsIgnoreCase(startDateInterval.name())) {
            optionalStartDate = Optional.ofNullable(options.getStartDateTypeIfCustom());
        }
        if ("CUSTOM".equalsIgnoreCase(endDateInterval.name())) {
            optionalEndDate = Optional.ofNullable(options.getEndDateTypeIfCustom());
        }
    
        // Use your existing method to get the date range
        return OptionsDateIntervalCalculator.getDateRange(startDateInterval, endDateInterval, optionalStartDate, optionalEndDate);
    }
}
