package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.model.Sex;
import com.tomaszstankowski.movieservice.repository.PersonRepository;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.InvalidPersonException;
import com.tomaszstankowski.movieservice.service.exception.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.GregorianCalendar;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository repo;

    private PersonService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Person person;

    @Before
    public void setup() {
        service = new PersonService(repo);
        person = new Person(
                "Janusz Gajos",
                new GregorianCalendar(1939, 8, 23).getTime(),
                "Dąbrowa Górnicza, Poland",
                Sex.MALE,
                new HashSet<Person.Proffesion>() {{
                    add(Person.Proffesion.ACTOR);
                }}
        );
    }

    @Test
    public void add_successful() {
        when(repo.findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace()))
                .thenReturn(null);

        service.addPerson(person);

        verify(repo, times(1))
                .findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace());
        verify(repo, times(1)).save(person);
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void add_whenPersonAlreadyExists_throwExc() {
        when(repo.findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace()))
                .thenReturn(person);
        exception.expect(PersonAlreadyExistsException.class);

        service.addPerson(person);

        verify(repo, times(1))
                .findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace());
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void add_whenBodyInvalid_throwExc() {
        Person body = new Person(
                "",
                person.getBirthDate(),
                person.getBirthPlace(),
                person.getSex(),
                person.getProffesions()
        );
        exception.expect(InvalidPersonException.class);

        service.addPerson(body);

        verifyZeroInteractions(repo);
    }

    @Test
    public void edit_successful() {
        when(repo.findOne(1L)).thenReturn(person);
        Person body = new Person(
                person.getName(),
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE,
                person.getProffesions()
        );

        service.editPerson(1L, body);

        verify(repo, times(1)).findOne(1L);
        verify(repo, times(1)).save(person);
        verifyNoMoreInteractions(repo);
        assertEquals(body.getBirthDate(), person.getBirthDate());
        assertEquals(body.getBirthPlace(), person.getBirthPlace());
    }

    @Test
    public void edit_whenPersonNotExists_throwExc() {
        when(repo.findOne(1L)).thenReturn(null);
        Person body = new Person(
                person.getName(),
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE,
                person.getProffesions()
        );
        exception.expect(PersonNotFoundException.class);

        service.editPerson(1L, body);

        verify(repo, times(1)).findOne(1L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void edit_whenBodyInvalid_throwExc() {
        Person body = new Person(
                null,
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE,
                person.getProffesions()
        );
        exception.expect(InvalidPersonException.class);

        service.editPerson(1L, body);

        verifyZeroInteractions(repo);
    }

    @Test
    public void delete_successful() {
        when(repo.findOne(1L)).thenReturn(person);

        service.removePerson(1L);

        verify(repo, times(1)).findOne(1L);
        verify(repo, times(1)).delete(1L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void delete_whenPersonNotExists_throwExc() {
        when(repo.findOne(1L)).thenReturn(null);
        exception.expect(PersonNotFoundException.class);

        service.removePerson(1L);

        verify(repo, times(1)).findOne(1L);
        verifyNoMoreInteractions(repo);
    }

}
