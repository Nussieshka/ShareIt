package com.nussia.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException() {
        this("Object not found");
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
