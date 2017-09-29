package com.tomaszstankowski.movieservice.service.exception;

public class UnexpectedTypeException extends RuntimeException {
    public UnexpectedTypeException(Class expected, Class found) {
        super("Expected: " + expected.getSimpleName() + " Found: " + found.getSimpleName());
    }
}
