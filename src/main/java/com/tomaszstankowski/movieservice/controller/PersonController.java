package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.ParticipationDTO;
import com.tomaszstankowski.movieservice.model.dto.PersonDTO;
import com.tomaszstankowski.movieservice.model.entity.Person;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.not_found.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.PersonNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.tomaszstankowski.movieservice.repository.specifications.PersonSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

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
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "birth_year_lt", required = false) Integer birthYearLt,
                                     @RequestParam(value = "birth_year_gt", required = false) Integer birthYearGt,
                                     @RequestParam(value = "sex", required = false) Sex sex,
                                     @RequestParam(value = "profession", required = false) Profession profession,
                                     @SortDefault("name") Sort sort) {

        Specifications<Person> specs = null;
        if (name != null)
            specs = where(nameContains(name));
        if (birthYearLt != null)
            specs = (specs == null) ? where(olderThan(birthYearLt)) : specs.and(olderThan(birthYearLt));
        if (birthYearGt != null)
            specs = (specs == null) ? where(youngerThan(birthYearGt)) : specs.and(youngerThan(birthYearGt));
        if (sex != null) {
            if (sex == Sex.MALE)
                specs = (specs == null) ? where(isMale()) : specs.and(isMale());
            if (sex == Sex.FEMALE)
                specs = (specs == null) ? where(isFemale()) : specs.and(isFemale());
        }
        if (profession != null)
            switch (profession) {
                case ACTOR:
                    specs = (specs == null) ? where(isActor()) : specs.and(isActor());
                    break;
                case DIRECTOR:
                    specs = (specs == null) ? where(isDirector()) : specs.and(isDirector());
                    break;
                case SCREENWRITER:
                    specs = (specs == null) ? where(isScreenwriter()) : specs.and(isScreenwriter());
                    break;
            }

        Page<Person> result = service.findAll(specs, page, sort);
        if (page >= result.getTotalPages() && page > 0)
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

    @GetMapping(path = "/{id}/participations")
    public List<ParticipationDTO> getPersonParticipations(@PathVariable("id") long personId,
                                                          @RequestParam(value = "role", required = false) Profession role) {
        return service.findParticipations(personId, role)
                .stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void editPerson(@PathVariable("id") long id, @RequestBody PersonDTO body) {
        service.editPerson(id, mapper.fromDTO(body));
    }

    @DeleteMapping(path = "/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deletePerson(@PathVariable("id") long id) {
        service.removePerson(id);
    }
}
