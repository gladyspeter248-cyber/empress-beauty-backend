package com.empress.beauty.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empress.beauty.entity.Customer;
import com.empress.beauty.service.CustomerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ==========================================
    // CUSTOMER REGISTRATION
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        try {

            Customer customer =
                    customerService.registerCustomer(
                            request.getFullName(),
                            request.getPhone(),
                            request.getEmail(),
                            request.getPassword()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "message",
                                    "Customer account created successfully",

                                    "customer",
                                    Map.of(
                                            "id",
                                            customer.getId(),

                                            "fullName",
                                            customer.getFullName(),

                                            "phone",
                                            customer.getPhone(),

                                            "email",
                                            customer.getEmail(),

                                            "role",
                                            customer.getRole()
                                    )
                            )
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // ==========================================
    // CUSTOMER LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        Customer customer =
                customerService.findByEmail(
                        request.getEmail()
                );

        if (customer == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }


        boolean passwordCorrect =
                customerService.checkPassword(
                        request.getPassword(),
                        customer.getPassword()
                );


        if (!passwordCorrect) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Login successful",

                        "customer",
                        Map.of(
                                "id",
                                customer.getId(),

                                "fullName",
                                customer.getFullName(),

                                "phone",
                                customer.getPhone(),

                                "email",
                                customer.getEmail(),

                                "role",
                                customer.getRole()
                        )
                )
        );
    }


    // ==========================================
    // REGISTRATION REQUEST
    // ==========================================

    public static class RegisterRequest {

        @NotBlank(
                message = "Full name is required"
        )
        @Size(
                min = 2,
                max = 100,
                message =
                        "Full name must be between 2 and 100 characters"
        )
        private String fullName;


        @NotBlank(
                message = "Phone number is required"
        )
        private String phone;


        @NotBlank(
                message = "Email is required"
        )
        @Email(
                message =
                        "Please provide a valid email address"
        )
        private String email;


        @NotBlank(
                message = "Password is required"
        )
        @Size(
                min = 8,
                message =
                        "Password must be at least 8 characters"
        )
        private String password;


        public RegisterRequest() {
        }


        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }


        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


    // ==========================================
    // LOGIN REQUEST
    // ==========================================

    public static class LoginRequest {

        @NotBlank(
                message = "Email is required"
        )
        @Email(
                message =
                        "Please provide a valid email address"
        )
        private String email;


        @NotBlank(
                message = "Password is required"
        )
        private String password;


        public LoginRequest() {
        }


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}