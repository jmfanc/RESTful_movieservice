package com.tomaszstankowski.movieservice.service.exception;

public class ShowNotFoundException extends RuntimeException {
    public ShowNotFoundException(long id) {
        super("Could not find movie/serial with id " + id + ".");
    }
}
