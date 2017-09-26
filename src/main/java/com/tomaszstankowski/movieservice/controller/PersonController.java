package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/people")
@EnableSpringDataWebSupport
public class PersonController {

    private final PersonService service;

    public PersonController(final PersonService service) {
        this.service = service;
    }

    @GetMapping
    public List<Person> getPeople(@RequestParam("page") int page,
                                  @SortDefault("name") Sort sort) {

        Page<Person> result = service.findAll(null, page, sort);
        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return result.getContent();
    }

    @GetMapping(path = "/{id}")
    public Person getPerson(@PathVariable("id") long id) {
        Person person = service.findPerson(id);
        if (person == null)
            throw new PersonNotFoundException(id);
        return person;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addPerson(@RequestBody Person body) {
        Person person = service.addPerson(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/people/{id}")
                .buildAndExpand(person.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{id}/edit")
    public ResponseEntity<?> editPerson(@PathVariable("id") long id, @RequestBody Person body) {
        service.editPerson(id, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{id}/delete")
    public ResponseEntity<?> deletePerson(@PathVariable("id") long id) {
        service.removePerson(id);
        return ResponseEntity.ok().build();
    }
}
