
package com.empress.beauty.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.empress.beauty.entity.Service;
import com.empress.beauty.repository.ServiceRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeServices(ServiceRepository serviceRepository) {

        return args -> {

            // Only add the services if the database is empty.
            // This prevents duplicate services every time the application restarts.
            if (serviceRepository.count() == 0) {

                serviceRepository.save(new Service(
                        "Hair Styling",
                        "Professional hair styling designed to give you a beautiful and confident look.",
                        new BigDecimal("25000"),
                        60,
                        "Hair",
                        true
                ));

                serviceRepository.save(new Service(
                        "Hair Braiding",
                        "Beautiful and professionally done braids using styles selected to suit your personality.",
                        new BigDecimal("30000"),
                        120,
                        "Hair",
                        true
                ));

                serviceRepository.save(new Service(
                        "Hair Treatment",
                        "Deep hair treatment that helps nourish, strengthen and improve the appearance of your hair.",
                        new BigDecimal("35000"),
                        90,
                        "Hair",
                        true
                ));

                serviceRepository.save(new Service(
                        "Manicure",
                        "Professional manicure treatment for clean, healthy and beautifully polished nails.",
                        new BigDecimal("20000"),
                        45,
                        "Nails",
                        true
                ));

                serviceRepository.save(new Service(
                        "Pedicure",
                        "Relaxing professional pedicure with careful nail and foot care.",
                        new BigDecimal("25000"),
                        60,
                        "Nails",
                        true
                ));

                serviceRepository.save(new Service(
                        "Gel Nails",
                        "Stylish and long-lasting gel nail treatment with a beautiful professional finish.",
                        new BigDecimal("30000"),
                        75,
                        "Nails",
                        true
                ));

                serviceRepository.save(new Service(
                        "Facial Treatment",
                        "Refreshing facial treatment designed to cleanse, nourish and rejuvenate the skin.",
                        new BigDecimal("40000"),
                        60,
                        "Beauty",
                        true
                ));

                serviceRepository.save(new Service(
                        "Makeup",
                        "Professional makeup application for everyday beauty, special occasions and events.",
                        new BigDecimal("50000"),
                        60,
                        "Makeup",
                        true
                ));

                serviceRepository.save(new Service(
                        "Bridal Makeup",
                        "Premium bridal makeup service designed to create an elegant and memorable wedding look.",
                        new BigDecimal("100000"),
                        120,
                        "Makeup",
                        true
                ));

                serviceRepository.save(new Service(
                        "Eyelash Extensions",
                        "Professional eyelash extensions for a fuller and more defined eye appearance.",
                        new BigDecimal("45000"),
                        90,
                        "Lashes",
                        true
                ));

                System.out.println("==========================================");
                System.out.println("EMPRESS BEAUTY SERVICES INITIALIZED");
                System.out.println("==========================================");
            }
        };
    }
}

