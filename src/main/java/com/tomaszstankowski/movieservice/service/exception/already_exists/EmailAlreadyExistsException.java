package com.tomaszstankowski.movieservice.service.exception.already_exists;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("User with email " + email + " already exists.");
    }
}
