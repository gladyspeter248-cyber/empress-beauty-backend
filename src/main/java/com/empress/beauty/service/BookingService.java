
package com.empress.beauty.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.empress.beauty.entity.Booking;
import com.empress.beauty.entity.Service;
import com.empress.beauty.exception.BookingException;
import com.empress.beauty.repository.BookingRepository;
import com.empress.beauty.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationService notificationService;

    public BookingService(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            NotificationService notificationService) {

        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // CREATE BOOKING
    // ==========================================

    public Booking createBooking(Booking booking) {

        Service service = serviceRepository
                .findById(booking.getServiceId())
                .orElseThrow(() ->
                        new BookingException(
                                "Selected service does not exist",
                                400
                        ));

        if (!Boolean.TRUE.equals(service.getAvailable())) {

            throw new BookingException(
                    "Selected service is currently unavailable",
                    400
            );
        }

        LocalDate bookingDate =
                booking.getBookingDate();

        LocalTime bookingTime =
                booking.getBookingTime();

        if (bookingDate == null) {

            throw new BookingException(
                    "Booking date is required",
                    400
            );
        }

        if (bookingTime == null) {

            throw new BookingException(
                    "Booking time is required",
                    400
            );
        }

        LocalDateTime bookingDateTime =
                LocalDateTime.of(
                        bookingDate,
                        bookingTime
                );

        LocalDateTime now =
                LocalDateTime.now();

        if (bookingDateTime.isBefore(now)) {

            throw new BookingException(
                    "You cannot book a date or time that has already passed",
                    400
            );
        }

        DayOfWeek day =
                bookingDate.getDayOfWeek();

        LocalTime openingTime;
        LocalTime closingTime;

        if (day == DayOfWeek.SUNDAY) {

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

        if (bookingTime.isBefore(openingTime)
                || !bookingTime.isBefore(closingTime)) {

            throw new BookingException(
                    "The selected time is outside Empress Beauty opening hours",
                    400
            );
        }

        Integer duration =
                service.getDurationMinutes();

        if (duration == null
                || duration <= 0) {

            throw new BookingException(
                    "This service has an invalid duration",
                    400
            );
        }

        LocalTime appointmentEnd =
                bookingTime.plusMinutes(duration);

        if (appointmentEnd.isAfter(closingTime)) {

            throw new BookingException(
                    "This appointment would extend beyond Empress Beauty opening hours",
                    400
            );
        }

        List<Booking> existingBookings =
                bookingRepository.findAll();

        for (Booking existing :
                existingBookings) {

            if ("CANCELLED".equalsIgnoreCase(
                    existing.getStatus())) {

                continue;
            }

            if (existing.getBookingDate() == null
                    || !existing.getBookingDate()
                            .equals(bookingDate)) {

                continue;
            }

            Service existingService =
                    serviceRepository
                            .findById(
                                    existing.getServiceId()
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
                    existing.getBookingTime();

            if (existingStart == null) {
                continue;
            }

            LocalTime existingEnd =
                    existingStart.plusMinutes(
                            existingDuration
                    );

            boolean overlaps =
                    bookingTime.isBefore(existingEnd)
                            &&
                    appointmentEnd.isAfter(existingStart);

            if (overlaps) {

                throw new BookingException(
                        "The selected time overlaps with another appointment. Please choose a different time.",
                        409
                );
            }
        }

        // ==========================================
        // SAVE BOOKING AS PENDING
        // ==========================================

        booking.setStatus("PENDING");

        Booking savedBooking =
                bookingRepository.save(booking);

        // ==========================================
        // ADMIN NOTIFICATION
        // ==========================================

        notificationService.createNotification(
                "BOOKING",
                "New booking received",
                (savedBooking.getCustomerName() != null
                        ? savedBooking.getCustomerName()
                        : "A customer")
                        + " requested an appointment.",
                savedBooking.getId()
        );

        // ==========================================
        // CUSTOMER NOTIFICATION
        // ==========================================

        if (savedBooking.getEmail() != null
                && !savedBooking.getEmail().isBlank()) {

            notificationService.createCustomerNotification(
                    savedBooking.getEmail().trim(),
                    "BOOKING",
                    "Booking request received",
                    "Your appointment request for "
                            + service.getName()
                            + " on "
                            + savedBooking.getBookingDate()
                            + " at "
                            + savedBooking.getBookingTime()
                            + " has been received and is pending confirmation.",
                    savedBooking.getId()
            );
        }

        return savedBooking;
    }

    // ==========================================
    // RESCHEDULE CUSTOMER BOOKING
    // ==========================================

    public Booking rescheduleCustomerBooking(
            Long bookingId,
            String customerEmail,
            LocalDate newDate,
            LocalTime newTime,
            Long newServiceId) {

        // ------------------------------------------
        // 1. FIND BOOKING
        // ------------------------------------------

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new BookingException(
                                        "Booking not found",
                                        404
                                ));

        // ------------------------------------------
        // 2. CHECK CUSTOMER EMAIL
        // ------------------------------------------

        if (customerEmail == null
                || customerEmail.isBlank()) {

            throw new BookingException(
                    "Customer email is required",
                    400
            );
        }

        // ------------------------------------------
        // 3. CHECK CUSTOMER OWNS BOOKING
        // ------------------------------------------

        if (booking.getEmail() == null
                || !booking.getEmail()
                        .equalsIgnoreCase(
                                customerEmail.trim()
                        )) {

            throw new BookingException(
                    "You are not authorized to modify this booking",
                    403
            );
        }

        // ------------------------------------------
        // 4. CHECK CURRENT STATUS
        // ------------------------------------------

        String currentStatus =
                booking.getStatus() == null
                        ? "PENDING"
                        : booking.getStatus().toUpperCase();

        if ("CANCELLED".equals(currentStatus)) {

            throw new BookingException(
                    "Cancelled bookings cannot be rescheduled",
                    400
            );
        }

        if (!"PENDING".equals(currentStatus)
                && !"CONFIRMED".equals(currentStatus)) {

            throw new BookingException(
                    "This booking cannot be rescheduled",
                    400
            );
        }

        // ------------------------------------------
        // 5. CHECK NEW DATE
        // ------------------------------------------

        if (newDate == null) {

            throw new BookingException(
                    "New booking date is required",
                    400
            );
        }

        // ------------------------------------------
        // 6. CHECK NEW TIME
        // ------------------------------------------

        if (newTime == null) {

            throw new BookingException(
                    "New booking time is required",
                    400
            );
        }

        // ------------------------------------------
        // 7. CHECK NEW SERVICE
        // ------------------------------------------

        if (newServiceId == null) {

            throw new BookingException(
                    "New service is required",
                    400
            );
        }

        Service newService =
                serviceRepository.findById(newServiceId)
                        .orElseThrow(() ->
                                new BookingException(
                                        "Selected service does not exist",
                                        400
                                ));

        // ------------------------------------------
        // 8. CHECK SERVICE AVAILABILITY
        // ------------------------------------------

        if (!Boolean.TRUE.equals(
                newService.getAvailable())) {

            throw new BookingException(
                    "Selected service is currently unavailable",
                    400
            );
        }

        // ------------------------------------------
        // 9. CHECK NEW DATE/TIME
        // ------------------------------------------

        LocalDateTime newBookingDateTime =
                LocalDateTime.of(
                        newDate,
                        newTime
                );

        if (newBookingDateTime.isBefore(
                LocalDateTime.now())) {

            throw new BookingException(
                    "You cannot reschedule to a date or time that has already passed",
                    400
            );
        }

        // ------------------------------------------
        // 10. CHECK OPENING HOURS
        // ------------------------------------------

        DayOfWeek newDay =
                newDate.getDayOfWeek();

        LocalTime openingTime;
        LocalTime closingTime;

        if (newDay == DayOfWeek.SUNDAY) {

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

        // ------------------------------------------
        // 11. CHECK START TIME
        // ------------------------------------------

        if (newTime.isBefore(openingTime)
                || !newTime.isBefore(closingTime)) {

            throw new BookingException(
                    "The selected time is outside Empress Beauty opening hours",
                    400
            );
        }

        // ------------------------------------------
        // 12. CHECK SERVICE DURATION
        // ------------------------------------------

        Integer newDuration =
                newService.getDurationMinutes();

        if (newDuration == null
                || newDuration <= 0) {

            throw new BookingException(
                    "The selected service has an invalid duration",
                    400
            );
        }

        LocalTime newAppointmentEnd =
                newTime.plusMinutes(
                        newDuration
                );

        // ------------------------------------------
        // 13. CHECK CLOSING TIME
        // ------------------------------------------

        if (newAppointmentEnd.isAfter(
                closingTime)) {

            throw new BookingException(
                    "This appointment would extend beyond Empress Beauty opening hours",
                    400
            );
        }

        // ------------------------------------------
        // 14. CHECK OVERLAPPING BOOKINGS
        // ------------------------------------------

        List<Booking> existingBookings =
                bookingRepository.findAll();

        for (Booking existing :
                existingBookings) {

            // Ignore the booking being edited

            if (existing.getId() != null
                    && existing.getId()
                            .equals(bookingId)) {

                continue;
            }

            // Ignore cancelled bookings

            if ("CANCELLED".equalsIgnoreCase(
                    existing.getStatus())) {

                continue;
            }

            // Only same date

            if (existing.getBookingDate() == null
                    || !existing.getBookingDate()
                            .equals(newDate)) {

                continue;
            }

            Service existingService =
                    serviceRepository.findById(
                            existing.getServiceId()
                    ).orElse(null);

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
                    existing.getBookingTime();

            if (existingStart == null) {
                continue;
            }

            LocalTime existingEnd =
                    existingStart.plusMinutes(
                            existingDuration
                    );

            boolean overlaps =
                    newTime.isBefore(existingEnd)
                            &&
                    newAppointmentEnd.isAfter(
                            existingStart
                    );

            if (overlaps) {

                throw new BookingException(
                        "The selected time overlaps with another appointment. Please choose a different time.",
                        409
                );
            }
        }

        // ------------------------------------------
        // 15. UPDATE BOOKING
        // ------------------------------------------

        booking.setServiceId(newServiceId);

        booking.setBookingDate(newDate);

        booking.setBookingTime(newTime);

        // ------------------------------------------
        // 16. SEND BACK FOR CONFIRMATION
        // ------------------------------------------

        booking.setStatus("PENDING");

        // ------------------------------------------
        // 17. SAVE
        // ------------------------------------------

        Booking updatedBooking =
                bookingRepository.save(booking);

        // ------------------------------------------
        // 18. ADMIN NOTIFICATION
        // ------------------------------------------

        notificationService.createNotification(
                "BOOKING",
                "Booking rescheduled",
                (updatedBooking.getCustomerName() != null
                        ? updatedBooking.getCustomerName()
                        : "A customer")
                        + " rescheduled booking #"
                        + updatedBooking.getId()
                        + ".",
                updatedBooking.getId()
        );

        // ------------------------------------------
        // 19. CUSTOMER NOTIFICATION
        // ------------------------------------------

        notificationService.createCustomerNotification(
                updatedBooking.getEmail().trim(),
                "BOOKING",
                "Booking rescheduled",
                "Your appointment has been rescheduled to "
                        + newService.getName()
                        + " on "
                        + updatedBooking.getBookingDate()
                        + " at "
                        + updatedBooking.getBookingTime()
                        + ". It is now pending confirmation.",
                updatedBooking.getId()
        );

        // ------------------------------------------
        // 20. RETURN UPDATED BOOKING
        // ------------------------------------------

        return updatedBooking;
    }
}

