package com.tomaszstankowski.movieservice.service.exception.not_found;

public class ParticipationNotFoundException extends RuntimeException {
    public ParticipationNotFoundException(long id) {
        super("Participation with id " + id + " could not be found.");
    }
}
