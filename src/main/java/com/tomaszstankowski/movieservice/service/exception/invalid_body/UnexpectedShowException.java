package com.tomaszstankowski.movieservice.service.exception.invalid_body;

public class UnexpectedShowException extends RuntimeException {
    public UnexpectedShowException(Class expected, Class found) {
        super("Expected " + expected.getSimpleName() + " but found " + found.getSimpleName() + ".");
    }
}
