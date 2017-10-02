package com.tomaszstankowski.movieservice.service.exception.invalid_body;

public class InvalidUserException extends RuntimeException {

    public InvalidUserException() {
        super("User body is invalid.");
    }
}
