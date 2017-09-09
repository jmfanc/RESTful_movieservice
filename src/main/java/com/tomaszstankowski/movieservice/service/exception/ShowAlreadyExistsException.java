package com.tomaszstankowski.movieservice.service.exception;

import java.util.Date;

public class ShowAlreadyExistsException extends RuntimeException {

    public ShowAlreadyExistsException(String title, Date releaseDate) {
        super(title + " released " + releaseDate + " already exists.");
    }
}
