package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class UnexpectedShowException extends RuntimeException {
    public UnexpectedShowException(Class expected, Class found) {
        super("Expected " + expected.getSimpleName() + " but found " + found.getSimpleName() + ".");
    }
}
