package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class InvalidPersonException extends RuntimeException {
    public InvalidPersonException() {
        super("Provided person is invalid.");
    }
}
