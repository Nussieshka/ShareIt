package com.nussia.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        this("Forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
