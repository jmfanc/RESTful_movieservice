package com.tomaszstankowski.movieservice.service.exception.invalid_body;

public class UnknownTypeException extends RuntimeException {
    public UnknownTypeException(Class superType) {
        super("Provided body has unknown type. Super type is " + superType.getSimpleName() + ".");
    }
}
