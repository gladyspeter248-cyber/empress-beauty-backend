package com.empress.beauty.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "testimonials")
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    private String serviceName;

    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // ==============================
    // CONSTRUCTORS
    // ==============================

    public Testimonial() {
    }

    public Testimonial(
            String customerName,
            Integer rating,
            String comment,
            String serviceName
    ) {
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.serviceName = serviceName;
        this.status = "PENDING";
    }


    // ==============================
    // PRE-PERSIST
    // ==============================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
    }


    // ==============================
    // PRE-UPDATE
    // ==============================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // ==============================
    // GETTERS AND SETTERS
    // ==============================

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


    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}