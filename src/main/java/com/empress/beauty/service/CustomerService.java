package com.empress.beauty.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.empress.beauty.entity.Customer;
import com.empress.beauty.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerCustomer(
            String fullName,
            String phone,
            String email,
            String password
    ) {

        email = email.trim().toLowerCase();
        phone = phone.trim();

        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        if (customerRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException(
                    "An account with this phone number already exists"
            );
        }

        Customer customer = new Customer();

        customer.setFullName(fullName.trim());
        customer.setPhone(phone);
        customer.setEmail(email);

        // NEVER store the customer's real password.
        customer.setPassword(
                passwordEncoder.encode(password)
        );

        customer.setRole("CUSTOMER");

        return customerRepository.save(customer);
    }

    public Customer findByEmail(String email) {

        return customerRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElse(null);
    }

    public boolean checkPassword(
            String rawPassword,
            String encodedPassword
    ) {

        return passwordEncoder.matches(
                rawPassword,
                encodedPassword
        );
    }
}