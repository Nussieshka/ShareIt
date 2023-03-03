package com.nussia.exception;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        this("Conflict");
    }

    public ConflictException(String message) {
        super(message);
    }
}
