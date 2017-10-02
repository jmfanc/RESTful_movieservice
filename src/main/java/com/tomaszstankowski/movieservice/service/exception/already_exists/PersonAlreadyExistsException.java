package com.tomaszstankowski.movieservice.service.exception.already_exists;

import com.tomaszstankowski.movieservice.model.entity.Person;

public class PersonAlreadyExistsException extends RuntimeException {
    public PersonAlreadyExistsException(Person person) {
        super(person.getName() + " born " + person.getBirthDate() + " in " + person.getBirthPlace() + " already exists.");
    }
}
