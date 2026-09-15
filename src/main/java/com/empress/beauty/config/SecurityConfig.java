package com.empress.beauty.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf ->
                        csrf.disable()
                )

                // ==========================================
                // CORS
                // ==========================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth

                        // ----------------------------------
                        // OPTIONS
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // ----------------------------------
                        // PUBLIC SERVICES
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/services",
                                "/api/services/**"
                        )
                        .permitAll()

                        // ----------------------------------
                        // CUSTOMER AUTHENTICATION
                        // ----------------------------------

                        .requestMatchers(
                                "/api/customers/register",
                                "/api/customers/login"
                        )
                        .permitAll()

                        // ----------------------------------
                        // CUSTOMER CREATE BOOKING
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        )
                        .permitAll()

                        // ----------------------------------
                        // AVAILABLE TIMES
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/available-times"
                        )
                        .permitAll()

                        // ----------------------------------
                        // CUSTOMER BOOKINGS
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/customer"
                        )
                        .permitAll()

                        // ----------------------------------
                        // CUSTOMER CANCEL
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/customer-cancel"
                        )
                        .permitAll()

                        // ----------------------------------
                        // CUSTOMER RESCHEDULE
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/customer-reschedule"
                        )
                        .permitAll()

                        // ==================================
                        // CUSTOMER NOTIFICATIONS
                        // ==================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/notifications/customer"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/notifications/customer/read-all"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/notifications/*/read"
                        )
                        .permitAll()

                        // ----------------------------------
                        // ADMIN ENDPOINTS
                        // ----------------------------------

                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // ADMIN BOOKING ENDPOINTS
                        // ----------------------------------

                        .requestMatchers(
                                "/api/bookings/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // ADMIN CREATE SERVICES
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/services/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // ADMIN UPDATE SERVICES
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/services/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // ADMIN DELETE SERVICES
                        // ----------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/services/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // ADMIN NOTIFICATIONS
                        // ----------------------------------

                        .requestMatchers(
                                "/api/notifications/**"
                        )
                        .hasRole("ADMIN")

                        // ----------------------------------
                        // OTHER REQUESTS
                        // ----------------------------------

                        .anyRequest()
                        .permitAll()
                )

                // ==========================================
                // HTTP BASIC
                // ==========================================

                .httpBasic(basic -> {});

        return http.build();
    }

    // ==========================================
    // CORS CONFIGURATION
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:4173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}