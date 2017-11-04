package com.tomaszstankowski.movieservice.service.exception.unproccessable;


public class InvalidShowException extends RuntimeException {
    public InvalidShowException() {
        super("Provided show is invalid.");
    }
}
