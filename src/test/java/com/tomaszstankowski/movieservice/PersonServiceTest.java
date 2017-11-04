package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.entity.Person;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.repository.ParticipationRepository;
import com.tomaszstankowski.movieservice.repository.PersonRepository;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.conflict.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.not_found.PersonNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidPersonException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepo;
    @Mock
    private ParticipationRepository participationRepo;

    private PersonService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Person person;

    @Before
    public void setup() {
        service = new PersonService(personRepo, participationRepo);
        person = new Person(
                "Janusz Gajos",
                new GregorianCalendar(1939, 8, 23).getTime(),
                "Dąbrowa Górnicza, Poland",
                Sex.MALE
        );
        person.getProfessions().add(Profession.ACTOR);
    }

    @Test
    public void add_successful() {
        when(personRepo.findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace()))
                .thenReturn(null);

        service.addPerson(person);

        verify(personRepo, times(1))
                .findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace());
        verify(personRepo, times(1)).save(person);
        verifyNoMoreInteractions(personRepo);
    }

    @Test
    public void add_whenPersonAlreadyExists_throwExc() {
        when(personRepo.findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace()))
                .thenReturn(person);
        exception.expect(PersonAlreadyExistsException.class);

        service.addPerson(person);

        verify(personRepo, times(1))
                .findByNameAndBirthDateAndBirthPlace(person.getName(), person.getBirthDate(), person.getBirthPlace());
        verifyNoMoreInteractions(personRepo);
    }

    @Test
    public void add_whenBodyInvalid_throwExc() {
        Person body = new Person(
                "",
                person.getBirthDate(),
                person.getBirthPlace(),
                person.getSex()
        );
        exception.expect(InvalidPersonException.class);

        service.addPerson(body);

        verifyZeroInteractions(personRepo);
    }

    @Test
    public void edit_successful() {
        when(personRepo.findOne(1L)).thenReturn(person);
        Person body = new Person(
                person.getName(),
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE
        );

        service.editPerson(1L, body);

        verify(personRepo, times(1)).findOne(1L);
        verify(personRepo, times(1)).save(person);
        verifyNoMoreInteractions(personRepo);
        assertEquals(body.getBirthDate(), person.getBirthDate());
        assertEquals(body.getBirthPlace(), person.getBirthPlace());
        assertTrue(person.getProfessions().isEmpty());
    }

    @Test
    public void edit_whenPersonNotExists_throwExc() {
        when(personRepo.findOne(1L)).thenReturn(null);
        Person body = new Person(
                person.getName(),
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE
        );
        exception.expect(PersonNotFoundException.class);

        service.editPerson(1L, body);

        verify(personRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(personRepo);
    }

    @Test
    public void edit_whenBodyInvalid_throwExc() {
        Person body = new Person(
                null,
                new GregorianCalendar(1939, 8, 24).getTime(),
                "Dąbrowa Górnicza, PL",
                Sex.MALE
        );
        exception.expect(InvalidPersonException.class);

        service.editPerson(1L, body);

        verifyZeroInteractions(personRepo);
    }

    @Test
    public void delete_successful() {
        when(personRepo.findOne(1L)).thenReturn(person);

        service.removePerson(1L);

        verify(personRepo, times(1)).findOne(1L);
        verify(personRepo, times(1)).delete(1L);
        verifyNoMoreInteractions(personRepo);
    }

    @Test
    public void delete_whenPersonNotExists_throwExc() {
        when(personRepo.findOne(1L)).thenReturn(null);
        exception.expect(PersonNotFoundException.class);

        service.removePerson(1L);

        verify(personRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(personRepo);
    }

}
