
package com.empress.beauty.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empress.beauty.entity.Notification;
import com.empress.beauty.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // ==========================================
    // ADMIN - GET ALL NOTIFICATIONS
    // ==========================================

    @GetMapping
    public List<Notification> getAllNotifications() {

        return notificationService.getAllNotifications();
    }

    // ==========================================
    // CUSTOMER - GET OWN NOTIFICATIONS
    // ==========================================

    @GetMapping("/customer")
    public List<Notification> getCustomerNotifications(
            @RequestParam String email) {

        return notificationService
                .getCustomerNotifications(email);
    }

    // ==========================================
    // MARK ONE NOTIFICATION AS READ
    // ==========================================

    @PutMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id) {

        notificationService.markAsRead(id);

        return "Notification marked as read";
    }

    // ==========================================
    // ADMIN - MARK ALL AS READ
    // ==========================================

    @PutMapping("/read-all")
    public String markAllAsRead() {

        notificationService.markAllAsRead();

        return "All admin notifications marked as read";
    }

    // ==========================================
    // CUSTOMER - MARK OWN NOTIFICATIONS AS READ
    // ==========================================

    @PutMapping("/customer/read-all")
    public String markCustomerNotificationsAsRead(
            @RequestParam String email) {

        notificationService
                .markCustomerNotificationsAsRead(email);

        return "Customer notifications marked as read";
    }

    // ==========================================
    // DELETE ALL NOTIFICATIONS
    // ==========================================

    @DeleteMapping
    public String deleteAllNotifications() {

        notificationService.deleteAllNotifications();

        return "All notifications deleted";
    }
}

