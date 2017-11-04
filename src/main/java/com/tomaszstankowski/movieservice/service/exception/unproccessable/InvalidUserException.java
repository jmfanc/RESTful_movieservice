package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class InvalidUserException extends RuntimeException {

    public InvalidUserException() {
        super("User body is invalid.");
    }
}
