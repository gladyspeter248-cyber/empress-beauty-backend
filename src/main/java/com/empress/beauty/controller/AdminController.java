package com.empress.beauty.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, String> dashboard(Authentication authentication) {

        return Map.of(
                "message", "Welcome to the Empress Beauty Admin Dashboard",
                "email", authentication.getName(),
                "role", "ADMIN"
        );
    }
}