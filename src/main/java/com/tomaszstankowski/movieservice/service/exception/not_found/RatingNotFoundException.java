package com.tomaszstankowski.movieservice.service.exception.not_found;

import com.tomaszstankowski.movieservice.model.entity.Show;
import com.tomaszstankowski.movieservice.model.entity.User;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(Show show, User user) {
        super("User " + user.getLogin() + " has not rated " + show.getTitle() + ".");
    }
}
