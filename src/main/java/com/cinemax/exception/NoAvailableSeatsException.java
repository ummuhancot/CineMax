package com.cinemax.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NoAvailableSeatsException extends RuntimeException {
    public NoAvailableSeatsException(String message) {
        super(message);
    }
}
