package com.tomaszstankowski.movieservice.service.exception.not_found;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String login) {
        super("Could not findOne user with login " + login + ".");
    }
}
