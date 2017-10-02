package com.tomaszstankowski.movieservice.service.exception.invalid_body;

public class InvalidPersonException extends RuntimeException {
    public InvalidPersonException() {
        super("Provided person is invalid.");
    }
}
