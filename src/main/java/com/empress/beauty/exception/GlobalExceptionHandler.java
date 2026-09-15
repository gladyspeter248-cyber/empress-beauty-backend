package com.empress.beauty.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, String>> handleBookingException(
            BookingException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getMessage()));
    }
}