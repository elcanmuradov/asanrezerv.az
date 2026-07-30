package com.asanrezerv.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bütün marşrutlar burada, Java DSL ilə. Servis ünvanları env/property-dən gəlir
 * (Docker-da servis adı, lokalda localhost). Marşrutlar YAML əvəzinə Java-da yazılıb ki,
 * Spring Cloud versiyaları arasında dəyişən YAML açarlarından asılı olmayaq.
 */
@Configuration
public class RouteConfig {

    @Value("${services.auth-uri}")
    private String authUri;

    @Value("${services.restaurant-uri}")
    private String restaurantUri;

    @Value("${services.reservation-uri}")
    private String reservationUri;

    @Value("${services.subscription-uri}")
    private String subscriptionUri;

    @Value("${services.staff-uri}")
    private String staffUri;

    @Value("${services.payment-uri}")
    private String paymentUri;

    @Value("${services.ai-uri}")
    private String aiUri;

    @Value("${services.search-uri}")
    private String searchUri;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // --- auth-service ---
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authUri))
                .route("auth-oauth2", r -> r
                        .path("/oauth2/**", "/login/oauth2/**")
                        .uri(authUri))
                // --- restaurant-service (public + manager + admin/restaurants) ---
                .route("restaurant-service", r -> r
                        .path(
                                "/api/restaurants/**",
                                "/api/branches/**",
                                "/api/admin/restaurants/**"
                        )
                        .uri(restaurantUri))

                .route("reservation-service", r -> r
                        .path("/api/reservations/**")
                        .uri(reservationUri))

                .route("subscription-service", r -> r
                        .path("/api/subscriptions/**",
                                "/api/admin/plans/**")
                        .uri(subscriptionUri))

                .route("ai-analysis-service", r -> r
                        .path("/api/ai/**")
                        .uri(aiUri))

                .route("search-service", r -> r
                        .path("/api/search/**")
                        .uri(searchUri))

                .route("staff-service",r ->  r
                        .path(
                                "/api/manager/**",
                                "/api/waiter/**"
                        )
                        .uri(staffUri)
                )
                .build();
    }
}
