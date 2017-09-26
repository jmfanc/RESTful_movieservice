package com.tomaszstankowski.movieservice.service.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(long id) {
        super("Could not find a person with id " + id + ".");
    }
}
