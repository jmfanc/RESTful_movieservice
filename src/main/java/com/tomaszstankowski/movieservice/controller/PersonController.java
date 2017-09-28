package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.model.dto.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.PersonDTO;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
@EnableSpringDataWebSupport
public class PersonController {

    private final PersonService service;
    private final ModelMapper mapper;

    public PersonController(PersonService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PersonDTO> getPeople(@RequestParam("page") int page,
                                     @SortDefault("name") Sort sort) {

        Page<Person> result = service.findAll(null, page, sort);
        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return result.getContent().stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public PersonDTO getPerson(@PathVariable("id") long id) {
        Person person = service.findPerson(id);
        if (person == null)
            throw new PersonNotFoundException(id);
        return mapper.fromEntity(person);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addPerson(@RequestBody PersonDTO body) {
        Person person = service.addPerson(mapper.fromDTO(body));
        URI location = ServletUriComponentsBuilder
                .fromPath("/people/{id}")
                .buildAndExpand(person.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editPerson(@PathVariable("id") long id, @RequestBody PersonDTO body) {
        service.editPerson(id, mapper.fromDTO(body));
    }

    @DeleteMapping(path = "/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deletePerson(@PathVariable("id") long id) {
        service.removePerson(id);
    }
}
