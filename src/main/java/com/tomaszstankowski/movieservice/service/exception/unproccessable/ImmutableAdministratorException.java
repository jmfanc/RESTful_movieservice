package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class ImmutableAdministratorException extends RuntimeException {
    public ImmutableAdministratorException() {
        super("Cannot change administrator role.");
    }
}
