package com.nussia.shareit;

import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class Parameters {
    private final Map<String, Object> parameters = new HashMap<>();

    public Parameters addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public static Parameters getInstance() {
        return new Parameters();
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    public Map<String, Object> get() {
        return parameters;
    }
}
