package com.tomaszstankowski.movieservice.model.enums;

public enum UserRole {
    USER, MOD, ADMIN;

    @Override
    public String toString() {
        return "ROLE_" + super.toString();
    }
}
