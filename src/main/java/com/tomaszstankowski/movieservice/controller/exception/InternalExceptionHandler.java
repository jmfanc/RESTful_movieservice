package com.tomaszstankowski.movieservice.controller.exception;

import com.tomaszstankowski.movieservice.service.exception.already_exists.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.already_exists.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.already_exists.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.*;
import com.tomaszstankowski.movieservice.service.exception.not_found.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class InternalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            UserNotFoundException.class,
            ShowNotFoundException.class,
            PageNotFoundException.class,
            PersonNotFoundException.class,
            ParticipationNotFoundException.class,
            RatingNotFoundException.class
    })
    public ResponseEntity<Object> handleResourceNotFoundExceptions(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(value = {
            InvalidUserException.class,
            InvalidShowException.class,
            InvalidPersonException.class,
            InvalidRatingException.class,
            UnexpectedTypeException.class,
            UnknownTypeException.class
    })
    public ResponseEntity<Object> handleInvalidBodyExceptions(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(value = {
            UserAlreadyExistsException.class,
            ShowAlreadyExistsException.class,
            PersonAlreadyExistsException.class
    })
    public ResponseEntity<Object> handleConflictExceptions(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request);
    }
}
