package com.tomaszstankowski.movieservice.service.exception.not_found;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(int page) {
        super("Page " + page + " not found.");
    }
}
