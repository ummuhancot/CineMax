package com.cinemax.exception;

public class BuiltInUserException extends RuntimeException {
    public BuiltInUserException(String message) {
        super(message);
    }

    public BuiltInUserException(String message, Throwable cause){super(message, cause);}
}
