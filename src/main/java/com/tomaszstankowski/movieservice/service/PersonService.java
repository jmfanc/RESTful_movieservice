package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.repository.PersonRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepo;

    public PersonService(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    public List<Person> findAll(Specification<Person> spec, Sort sort) {
        return personRepo.findAll(spec, sort);
    }

}
