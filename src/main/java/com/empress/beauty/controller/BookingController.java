
package com.empress.beauty.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empress.beauty.entity.Booking;
import com.empress.beauty.entity.Service;
import com.empress.beauty.repository.BookingRepository;
import com.empress.beauty.repository.ServiceRepository;
import com.empress.beauty.service.BookingService;
import com.empress.beauty.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final NotificationService notificationService;

    public BookingController(
            BookingRepository bookingRepository,
            BookingService bookingService,
            ServiceRepository serviceRepository,
            NotificationService notificationService) {

        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.serviceRepository = serviceRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // GET ALL BOOKINGS - ADMIN
    // ==========================================

    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    // ==========================================
    // GET CUSTOMER BOOKINGS
    // ==========================================

    @GetMapping("/customer")
    public List<Booking> getCustomerBookings(
            @RequestParam String email) {

        return bookingRepository
                .findByEmailIgnoreCase(email);
    }

    // ==========================================
    // CANCEL CUSTOMER BOOKING
    // ==========================================

    @PutMapping("/{id}/customer-cancel")
    public Booking cancelCustomerBooking(
            @PathVariable Long id,
            @RequestParam String email) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                                ));

        if (booking.getEmail() == null
                || !booking.getEmail()
                        .equalsIgnoreCase(
                                email.trim()
                        )) {

            throw new RuntimeException(
                    "You are not authorized to cancel this booking"
            );
        }

        String currentStatus =
                booking.getStatus() == null
                        ? "PENDING"
                        : booking.getStatus().toUpperCase();

        if ("CANCELLED".equals(currentStatus)) {

            throw new RuntimeException(
                    "This booking has already been cancelled"
            );
        }

        if (!"PENDING".equals(currentStatus)
                && !"CONFIRMED".equals(currentStatus)) {

            throw new RuntimeException(
                    "This booking cannot be cancelled"
            );
        }

        // ------------------------------------------
        // CANCEL BOOKING
        // ------------------------------------------

        booking.setStatus("CANCELLED");

        Booking cancelledBooking =
                bookingRepository.save(booking);

        // ------------------------------------------
        // CUSTOMER NOTIFICATION
        // ------------------------------------------

        if (cancelledBooking.getEmail() != null
                && !cancelledBooking.getEmail().isBlank()) {

            notificationService.createCustomerNotification(
                    cancelledBooking.getEmail().trim(),
                    "BOOKING",
                    "Booking cancelled",
                    "Your "
                            + getServiceName(
                                    cancelledBooking.getServiceId()
                            )
                            + " appointment on "
                            + cancelledBooking.getBookingDate()
                            + " at "
                            + cancelledBooking.getBookingTime()
                            + " has been cancelled.",
                    cancelledBooking.getId()
            );
        }

        // ------------------------------------------
        // ADMIN NOTIFICATION
        // ------------------------------------------

        notificationService.createNotification(
                "BOOKING",
                "Booking cancelled",
                (cancelledBooking.getCustomerName() != null
                        ? cancelledBooking.getCustomerName()
                        : "A customer")
                        + " cancelled booking #"
                        + cancelledBooking.getId()
                        + ".",
                cancelledBooking.getId()
        );

        return cancelledBooking;
    }

    // ==========================================
    // RESCHEDULE CUSTOMER BOOKING
    // ==========================================

    @PutMapping("/{id}/customer-reschedule")
    public Booking rescheduleCustomerBooking(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestBody Map<String, Object> request) {

        // ------------------------------------------
        // NEW SERVICE
        // ------------------------------------------

        Long newServiceId = null;

        Object serviceIdValue =
                request.get("serviceId");

        if (serviceIdValue != null) {

            if (serviceIdValue instanceof Number) {

                newServiceId =
                        ((Number) serviceIdValue)
                                .longValue();

            } else {

                try {

                    newServiceId =
                            Long.parseLong(
                                    serviceIdValue.toString()
                            );

                } catch (NumberFormatException e) {

                    throw new RuntimeException(
                            "Invalid service ID"
                    );
                }
            }
        }

        // ------------------------------------------
        // NEW DATE
        // ------------------------------------------

        LocalDate newDate = null;

        Object dateValue =
                request.get("bookingDate");

        if (dateValue != null
                && !dateValue.toString().isBlank()) {

            try {

                newDate =
                        LocalDate.parse(
                                dateValue.toString()
                        );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Invalid booking date. Use YYYY-MM-DD"
                );
            }
        }

        // ------------------------------------------
        // NEW TIME
        // ------------------------------------------

        LocalTime newTime = null;

        Object timeValue =
                request.get("bookingTime");

        if (timeValue != null
                && !timeValue.toString().isBlank()) {

            try {

                newTime =
                        LocalTime.parse(
                                timeValue.toString()
                        );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Invalid booking time. Use HH:mm"
                );
            }
        }

        // ------------------------------------------
        // CALL BOOKING SERVICE
        // ------------------------------------------

        return bookingService.rescheduleCustomerBooking(
                id,
                email,
                newDate,
                newTime,
                newServiceId
        );
    }

    // ==========================================
    // GET AVAILABLE TIMES
    // ==========================================

    @GetMapping("/available-times")
    public Map<String, Object> getAvailableTimes(
            @RequestParam Long serviceId,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date) {

        Service service =
                serviceRepository.findById(serviceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Selected service does not exist"
                                ));

        if (!Boolean.TRUE.equals(
                service.getAvailable())) {

            return Map.of(
                    "serviceId",
                    serviceId,
                    "date",
                    date,
                    "available",
                    false,
                    "slots",
                    List.of()
            );
        }

        Integer duration =
                service.getDurationMinutes();

        if (duration == null
                || duration <= 0) {

            return Map.of(
                    "serviceId",
                    serviceId,
                    "date",
                    date,
                    "available",
                    false,
                    "slots",
                    List.of()
            );
        }

        LocalTime openingTime;
        LocalTime closingTime;

        if (date.getDayOfWeek().getValue() == 7) {

            openingTime =
                    LocalTime.of(9, 0);

            closingTime =
                    LocalTime.of(18, 0);

        } else {

            openingTime =
                    LocalTime.of(8, 0);

            closingTime =
                    LocalTime.of(20, 0);
        }

        List<Booking> existingBookings =
                bookingRepository.findAll();

        List<Booking> bookingsForDate =
                new ArrayList<>();

        for (Booking booking :
                existingBookings) {

            if (booking.getBookingDate() == null) {
                continue;
            }

            if (!booking.getBookingDate()
                    .equals(date)) {

                continue;
            }

            if ("CANCELLED".equalsIgnoreCase(
                    booking.getStatus())) {

                continue;
            }

            bookingsForDate.add(booking);
        }

        List<String> availableSlots =
                new ArrayList<>();

        LocalTime slotStart =
                openingTime;

        while (true) {

            LocalTime slotEnd =
                    slotStart.plusMinutes(duration);

            if (slotEnd.isAfter(closingTime)) {
                break;
            }

            // Don't show past times today

            if (date.equals(LocalDate.now())) {

                LocalTime now =
                        LocalTime.now();

                if (!slotStart.isAfter(now)) {

                    slotStart =
                            slotStart.plusMinutes(30);

                    continue;
                }
            }

            boolean overlaps = false;

            for (Booking existingBooking :
                    bookingsForDate) {

                if (existingBooking
                        .getBookingTime() == null) {

                    continue;
                }

                Service existingService =
                        serviceRepository
                                .findById(
                                        existingBooking
                                                .getServiceId()
                                )
                                .orElse(null);

                if (existingService == null) {
                    continue;
                }

                Integer existingDuration =
                        existingService
                                .getDurationMinutes();

                if (existingDuration == null
                        || existingDuration <= 0) {

                    continue;
                }

                LocalTime existingStart =
                        existingBooking
                                .getBookingTime();

                LocalTime existingEnd =
                        existingStart.plusMinutes(
                                existingDuration
                        );

                if (slotStart.isBefore(existingEnd)
                        && slotEnd.isAfter(
                                existingStart)) {

                    overlaps = true;

                    break;
                }
            }

            if (!overlaps) {

                availableSlots.add(
                        slotStart.toString()
                );
            }

            slotStart =
                    slotStart.plusMinutes(30);
        }

        return Map.of(
                "serviceId",
                serviceId,
                "date",
                date,
                "available",
                !availableSlots.isEmpty(),
                "slots",
                availableSlots
        );
    }

    // ==========================================
    // GET BOOKING BY ID - ADMIN
    // ==========================================

    @GetMapping("/{id}")
    public Booking getBookingById(
            @PathVariable Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"
                        ));
    }

    // ==========================================
    // CREATE BOOKING
    // ==========================================

    @PostMapping
    public Booking createBooking(
            @Valid @RequestBody Booking booking) {

        return bookingService.createBooking(booking);
    }

    // ==========================================
    // UPDATE COMPLETE BOOKING - ADMIN
    // ==========================================

    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                        ));

        booking.setCustomerName(
                updatedBooking.getCustomerName()
        );

        booking.setPhone(
                updatedBooking.getPhone()
        );

        booking.setEmail(
                updatedBooking.getEmail()
        );

        booking.setServiceId(
                updatedBooking.getServiceId()
        );

        booking.setBookingDate(
                updatedBooking.getBookingDate()
        );

        booking.setBookingTime(
                updatedBooking.getBookingTime()
        );

        booking.setStatus(
                updatedBooking.getStatus()
        );

        return bookingRepository.save(booking);
    }

    // ==========================================
    // UPDATE BOOKING STATUS - ADMIN
    // ==========================================

    @PutMapping("/{id}/status")
    public Booking updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String newStatus =
                request.get("status");

        if (newStatus == null
                || newStatus.isBlank()) {

            throw new RuntimeException(
                    "Booking status is required"
            );
        }

        newStatus =
                newStatus.toUpperCase();

        if (!newStatus.equals("PENDING")
                && !newStatus.equals("CONFIRMED")
                && !newStatus.equals("CANCELLED")) {

            throw new RuntimeException(
                    "Invalid booking status. Use PENDING, CONFIRMED or CANCELLED"
            );
        }

        // ------------------------------------------
        // GET EXISTING BOOKING FIRST
        // ------------------------------------------

        Booking existingBooking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                                ));

        String oldStatus =
                existingBooking.getStatus() == null
                        ? "PENDING"
                        : existingBooking.getStatus().toUpperCase();

        // ------------------------------------------
        // UPDATE STATUS
        // ------------------------------------------

        int updatedRows =
                bookingRepository.updateStatus(
                        id,
                        newStatus
                );

        if (updatedRows == 0) {

            throw new RuntimeException(
                    "Booking not found"
            );
        }

        Booking updatedBooking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                                ));

        // ------------------------------------------
        // CUSTOMER CONFIRMATION NOTIFICATION
        // ------------------------------------------

        if ("CONFIRMED".equals(newStatus)
                && !"CONFIRMED".equals(oldStatus)
                && updatedBooking.getEmail() != null
                && !updatedBooking.getEmail().isBlank()) {

            notificationService.createCustomerNotification(
                    updatedBooking.getEmail().trim(),
                    "BOOKING",
                    "Booking confirmed",
                    "Great news! Your "
                            + getServiceName(
                                    updatedBooking.getServiceId()
                            )
                            + " appointment on "
                            + updatedBooking.getBookingDate()
                            + " at "
                            + updatedBooking.getBookingTime()
                            + " has been confirmed by Empress Beauty.",
                    updatedBooking.getId()
            );
        }

        // ------------------------------------------
        // CUSTOMER CANCELLATION NOTIFICATION
        // ------------------------------------------

        if ("CANCELLED".equals(newStatus)
                && !"CANCELLED".equals(oldStatus)
                && updatedBooking.getEmail() != null
                && !updatedBooking.getEmail().isBlank()) {

            notificationService.createCustomerNotification(
                    updatedBooking.getEmail().trim(),
                    "BOOKING",
                    "Booking cancelled",
                    "Your "
                            + getServiceName(
                                    updatedBooking.getServiceId()
                            )
                            + " appointment on "
                            + updatedBooking.getBookingDate()
                            + " at "
                            + updatedBooking.getBookingTime()
                            + " has been cancelled by Empress Beauty.",
                    updatedBooking.getId()
            );
        }

        // ------------------------------------------
        // ADMIN NOTIFICATION
        // ------------------------------------------

        if (!oldStatus.equals(newStatus)) {

            notificationService.createNotification(
                    "BOOKING",
                    "Booking status updated",
                    (updatedBooking.getCustomerName() != null
                            ? updatedBooking.getCustomerName()
                            : "A customer")
                            + "'s booking #"
                            + updatedBooking.getId()
                            + " changed from "
                            + oldStatus
                            + " to "
                            + newStatus
                            + ".",
                    updatedBooking.getId()
            );
        }

        return updatedBooking;
    }

    // ==========================================
    // DELETE BOOKING - ADMIN
    // ==========================================

    @DeleteMapping("/{id}")
    public String deleteBooking(
            @PathVariable Long id) {

        bookingRepository.deleteById(id);

        return "Booking deleted successfully";
    }

    // ==========================================
    // HELPER - GET SERVICE NAME
    // ==========================================

    private String getServiceName(Long serviceId) {

        if (serviceId == null) {
            return "beauty service";
        }

        return serviceRepository.findById(serviceId)
                .map(Service::getName)
                .orElse("beauty service");
    }
}

