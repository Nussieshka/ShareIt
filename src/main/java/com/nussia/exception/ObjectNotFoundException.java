package com.nussia.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException() {
        super();
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String item, Long id) {
        super(item + " with ID " + id + " does not exist.");
    }
}
