package com.nussia.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        return getErrorResponse(e.getMessage());
    }

    private ErrorResponse getErrorResponse(String errorMessage) {
        if (errorMessage == null) {
            return null;
        } else {
            log.error(errorMessage);
            return new ErrorResponse(errorMessage);
        }
    }

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        @JsonProperty("error")
        private final String errorMessage;
    }
}
