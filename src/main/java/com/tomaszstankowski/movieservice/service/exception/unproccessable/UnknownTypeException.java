package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class UnknownTypeException extends RuntimeException {
    public UnknownTypeException(Class superType) {
        super("Provided body has unknown type. Super type is " + superType.getSimpleName() + ".");
    }
}
