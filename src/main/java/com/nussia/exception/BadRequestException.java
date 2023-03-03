package com.nussia.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        this("Bad request");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
