package com.tomaszstankowski.movieservice.service.exception.not_found;

public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(long id) {
        super("Could not findUser show with id " + id + ".");
    }
}
