package com.tomaszstankowski.movieservice.service.exception.unproccessable;

public class InvalidRatingException extends RuntimeException {
    public InvalidRatingException(short rating) {
        super("Provided rating is invalid - " + rating + ". Value should be between 1 and 10.");
    }
}
