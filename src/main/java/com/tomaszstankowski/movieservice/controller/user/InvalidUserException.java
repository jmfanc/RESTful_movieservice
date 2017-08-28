package com.tomaszstankowski.movieservice.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Provided User is invalid.")
public class InvalidUserException extends RuntimeException {
}
