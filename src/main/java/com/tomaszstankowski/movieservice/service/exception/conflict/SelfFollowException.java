package com.tomaszstankowski.movieservice.service.exception.conflict;

public class SelfFollowException extends RuntimeException {
    public SelfFollowException() {
        super("Cannot follow yourself.");
    }
}
