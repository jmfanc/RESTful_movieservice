package com.tomaszstankowski.movieservice.model.dto;

public class AbstractDTOException extends RuntimeException {
    public AbstractDTOException(Class c) {
        super("Cannot create instance of abstract type " + c.getSimpleName() + " . Json body lacks of properties.");
    }
}
