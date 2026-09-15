
package com.empress.beauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.empress.beauty.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // ==========================================
    // ADMIN NOTIFICATIONS
    // ==========================================

    List<Notification> findAllByOrderByCreatedAtDesc();

    // ==========================================
    // CUSTOMER NOTIFICATIONS
    // ==========================================

    List<Notification>
    findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(
            String customerEmail
    );

    // ==========================================
    // MARK ONE AS READ
    // ==========================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.read = true
            WHERE n.id = :id
            """)
    int markAsRead(@Param("id") Long id);

    // ==========================================
    // MARK ALL ADMIN NOTIFICATIONS AS READ
    // ==========================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.read = true
            WHERE n.read = false
            AND n.customerEmail IS NULL
            """)
    int markAllAsRead();

    // ==========================================
    // MARK CUSTOMER NOTIFICATIONS AS READ
    // ==========================================

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.read = true
            WHERE n.read = false
            AND LOWER(n.customerEmail) = LOWER(:customerEmail)
            """)
    int markCustomerNotificationsAsRead(
            @Param("customerEmail") String customerEmail
    );

    // ==========================================
    // DELETE ALL NOTIFICATIONS
    // ==========================================

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n")
    void deleteAllNotifications();
}

