package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.repository.PersonRepository;
import com.tomaszstankowski.movieservice.service.exception.InvalidPersonException;
import com.tomaszstankowski.movieservice.service.exception.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepo;

    public PersonService(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    public Person findPerson(long id) {
        return personRepo.findOne(id);
    }

    public Page<Person> findAll(Specification<Person> spec, int page, Sort sort) {
        return personRepo.findAll(spec, createPageable(page, sort));
    }

    public Person addPerson(Person body) {
        validatePerson(body);
        checkIfPersonAlreadyExists(body);
        Person person = new Person(
                body.getName(),
                body.getBirthDate(),
                body.getBirthPlace(),
                body.getSex(),
                body.getProffesions()
        );
        return personRepo.save(person);
    }

    public void editPerson(long id, Person body) {
        validatePerson(body);
        Person person = personRepo.findOne(id);
        if (person == null)
            throw new PersonNotFoundException(id);
        person.setName(body.getName());
        person.setBirthDate(body.getBirthDate());
        person.setBirthPlace(body.getBirthPlace());
        person.setProffesions(body.getProffesions());
        personRepo.save(person);
    }

    public void removePerson(long id) {
        if (personRepo.findOne(id) == null)
            throw new PersonNotFoundException(id);
        personRepo.delete(id);
    }

    private void checkIfPersonAlreadyExists(Person person) {
        if (null != personRepo.findByNameAndBirthDateAndBirthPlace(
                person.getName(), person.getBirthDate(), person.getBirthPlace()))
            throw new PersonAlreadyExistsException(person);
    }

    private void validatePerson(Person person) {
        if (person.getName() == null || person.getName().isEmpty())
            throw new InvalidPersonException();
    }

    private Pageable createPageable(int page, Sort sort) {
        return new PageRequest(page, 10, sort);
    }
}
