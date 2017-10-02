package com.tomaszstankowski.movieservice.service.exception.already_exists;

import java.util.Date;

public class ShowAlreadyExistsException extends RuntimeException {

    public ShowAlreadyExistsException(String title, Date releaseDate) {
        super(title + " released " + releaseDate + " already exists.");
    }
}
