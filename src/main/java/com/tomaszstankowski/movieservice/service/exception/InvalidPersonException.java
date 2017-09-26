package com.tomaszstankowski.movieservice.service.exception;

public class InvalidPersonException extends RuntimeException {
    public InvalidPersonException() {
        super("Provided person is invalid.");
    }
}
