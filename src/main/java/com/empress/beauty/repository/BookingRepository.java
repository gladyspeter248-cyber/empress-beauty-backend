package com.empress.beauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empress.beauty.entity.Booking;

import jakarta.transaction.Transactional;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {


    // =========================================================
    // FIND BOOKINGS BY CUSTOMER EMAIL
    // =========================================================

    List<Booking> findByEmailIgnoreCase(String email);


    // =========================================================
    // UPDATE BOOKING STATUS
    // =========================================================

    @Modifying
    @Transactional
    @Query("""
        UPDATE Booking b
        SET b.status = :status
        WHERE b.id = :id
    """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );
}