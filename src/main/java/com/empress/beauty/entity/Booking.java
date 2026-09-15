package com.empress.beauty.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ================================
    // CUSTOMER FULL NAME
    // ================================

    @NotBlank(message = "Full name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Full name must be between 2 and 100 characters"
    )
    @Pattern(
        regexp = "^[A-Za-z]+(?:[ '-][A-Za-z]+)+$",
        message = "Please enter your full name using at least two names"
    )
    @Column(nullable = false)
    private String customerName;


    // ================================
    // PHONE NUMBER
    // ================================

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(\\+255|255|0)[67][0-9]{8}$",
        message = "Please provide a valid Tanzanian phone number"
    )
    @Column(nullable = false)
    private String phone;


    // ================================
    // EMAIL
    // ================================

    @NotBlank(message = "Email is required")
    @Email(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Please provide a valid email address"
    )
    @Column(nullable = false)
    private String email;


    // ================================
    // SERVICE
    // ================================

    @NotNull(message = "Service is required")
    @Column(nullable = false)
    private Long serviceId;


    // ================================
    // BOOKING DATE
    // ================================

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(
        message = "Booking date cannot be in the past"
    )
    @Column(nullable = false)
    private LocalDate bookingDate;


    // ================================
    // BOOKING TIME
    // ================================

    @NotNull(message = "Booking time is required")
    @Column(nullable = false)
    private LocalTime bookingTime;


    // ================================
    // BOOKING STATUS
    // ================================

    @Column(nullable = false)
    private String status = "PENDING";


    // ================================
    // EMPTY CONSTRUCTOR
    // ================================

    public Booking() {
    }


    // ================================
    // FULL CONSTRUCTOR
    // ================================

    public Booking(
            String customerName,
            String phone,
            String email,
            Long serviceId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String status) {

        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.serviceId = serviceId;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status = status;
    }


    // ================================
    // GETTERS AND SETTERS
    // ================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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


    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }


    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }


    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}