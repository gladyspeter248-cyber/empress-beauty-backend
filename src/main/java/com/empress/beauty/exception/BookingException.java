package com.empress.beauty.exception;

public class BookingException extends RuntimeException {

    private final int statusCode;

    public BookingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}