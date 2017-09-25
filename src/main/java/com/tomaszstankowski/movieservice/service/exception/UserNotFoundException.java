package com.tomaszstankowski.movieservice.service.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String login) {
        super("Could not findOne user with login " + login + ".");
    }
}
