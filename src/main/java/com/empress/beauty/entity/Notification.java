
package com.empress.beauty.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column
    private Long bookingId;

    /*
     * Customer email identifies which customer owns
     * this notification.
     *
     * Admin notifications will have this field as null.
     */
    @Column
    private String customerEmail;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(
            String type,
            String title,
            String message,
            Long bookingId,
            String customerEmail,
            boolean read,
            LocalDateTime createdAt) {

        this.type = type;
        this.title = title;
        this.message = message;
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

