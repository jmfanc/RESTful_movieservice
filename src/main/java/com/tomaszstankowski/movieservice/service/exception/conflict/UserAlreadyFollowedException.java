package com.tomaszstankowski.movieservice.service.exception.conflict;

public class UserAlreadyFollowedException extends RuntimeException {
    public UserAlreadyFollowedException(String followed, String follower) {
        super("User " + followed + " is already followed by " + follower + ".");
    }
}
