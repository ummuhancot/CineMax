package com.cinemax.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SeatsNotLoadedException extends RuntimeException {
    public SeatsNotLoadedException(String message) {
        super(message);
    }
}
