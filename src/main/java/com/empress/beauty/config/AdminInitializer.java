package com.empress.beauty.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.empress.beauty.entity.Admin;
import com.empress.beauty.repository.AdminRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@empressbeauty.com";
            String adminPassword = "Admin@12345";

            if (adminRepository.findByEmail(adminEmail).isEmpty()) {

                Admin admin = new Admin();

                admin.setEmail(adminEmail);
                admin.setPassword(
                        passwordEncoder.encode(adminPassword)
                );
                admin.setRole("ADMIN");

                adminRepository.save(admin);

                System.out.println("=================================");
                System.out.println("EMPRESS BEAUTY ADMIN CREATED");
                System.out.println("Email: " + adminEmail);
                System.out.println("=================================");

            } else {

                System.out.println("EMPRESS BEAUTY ADMIN ALREADY EXISTS");

            }
        };
    }
}