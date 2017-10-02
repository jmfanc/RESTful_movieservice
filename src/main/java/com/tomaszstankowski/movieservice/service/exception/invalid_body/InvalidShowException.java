package com.tomaszstankowski.movieservice.service.exception.invalid_body;


public class InvalidShowException extends RuntimeException {
    public InvalidShowException() {
        super("Provided show is invalid.");
    }
}
