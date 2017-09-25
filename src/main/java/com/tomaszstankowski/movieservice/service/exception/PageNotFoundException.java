package com.tomaszstankowski.movieservice.service.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(int page) {
        super("Page " + page + " not found.");
    }
}
