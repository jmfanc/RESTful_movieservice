package com.tomaszstankowski.movieservice.service.exception;


public class InvalidShowException extends RuntimeException {
    public InvalidShowException() {
        super("Show is invalid.");
    }
}
