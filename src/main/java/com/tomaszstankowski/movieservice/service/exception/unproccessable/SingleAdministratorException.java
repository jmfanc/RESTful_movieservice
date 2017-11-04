package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class SingleAdministratorException extends RuntimeException {
    public SingleAdministratorException() {
        super("Cannot change role to ADMIN. Only one administrator can be specified.");
    }
}
