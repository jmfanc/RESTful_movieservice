package com.tomaszstankowski.movieservice.service.exception.not_found;

public class FollowerNotFoundException extends RuntimeException {
    public FollowerNotFoundException(String followed, String follower) {
        super("User " + follower + " does not follow user " + followed + ".");
    }
}
