package com.tomaszstankowski.movieservice.service.exception.conflict;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String login) {
        super("User with login " + login + " already exists.");
    }
}
