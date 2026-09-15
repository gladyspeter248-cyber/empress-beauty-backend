
package com.empress.beauty.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.empress.beauty.entity.Notification;
import com.empress.beauty.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // ==========================================
    // CREATE ADMIN NOTIFICATION
    // ==========================================

    public Notification createNotification(
            String type,
            String title,
            String message,
            Long bookingId) {

        Notification notification = new Notification();

        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setBookingId(bookingId);

        // Null means this is an ADMIN notification.
        notification.setCustomerEmail(null);

        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // ==========================================
    // CREATE CUSTOMER NOTIFICATION
    // ==========================================

    public Notification createCustomerNotification(
            String customerEmail,
            String type,
            String title,
            String message,
            Long bookingId) {

        Notification notification = new Notification();

        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setBookingId(bookingId);

        notification.setCustomerEmail(customerEmail);

        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // ==========================================
    // GET ALL ADMIN NOTIFICATIONS
    // ==========================================

    public List<Notification> getAllNotifications() {

        return notificationRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // ==========================================
    // GET CUSTOMER NOTIFICATIONS
    // ==========================================

    public List<Notification> getCustomerNotifications(
            String customerEmail) {

        if (customerEmail == null ||
                customerEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Customer email is required"
            );
        }

        return notificationRepository
                .findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(
                        customerEmail.trim()
                );
    }

    // ==========================================
    // MARK ONE AS READ
    // ==========================================

    public void markAsRead(Long id) {

        int updatedRows =
                notificationRepository.markAsRead(id);

        if (updatedRows == 0) {

            throw new RuntimeException(
                    "Notification not found"
            );
        }
    }

    // ==========================================
    // MARK ALL ADMIN NOTIFICATIONS AS READ
    // ==========================================

    public void markAllAsRead() {

        notificationRepository.markAllAsRead();
    }

    // ==========================================
    // MARK CUSTOMER NOTIFICATIONS AS READ
    // ==========================================

    public void markCustomerNotificationsAsRead(
            String customerEmail) {

        if (customerEmail == null ||
                customerEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Customer email is required"
            );
        }

        notificationRepository
                .markCustomerNotificationsAsRead(
                        customerEmail.trim()
                );
    }

    // ==========================================
    // DELETE ALL NOTIFICATIONS
    // ==========================================

    public void deleteAllNotifications() {

        notificationRepository.deleteAllNotifications();
    }
}

