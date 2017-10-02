package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.already_exists.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidUserException;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository repo;

    private UserService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private User user;

    @Before
    public void setup() {
        service = new UserService(repo);
        user = new User(
                "janusz111",
                "Janusz Cyps",
                "jan@usz.pl",
                Sex.MALE
        );
    }

    @Test
    public void add_successful() {
        when(repo.findOne(user.getLogin())).thenReturn(null);

        service.add(user);

        verify(repo, times(1)).findOne(user.getLogin());
        verify(repo, times(1)).save(user);
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void add_whenUserAlreadyExists_throwExc() {
        when(repo.findOne(user.getLogin())).thenReturn(user);
        exception.expect(UserAlreadyExistsException.class);

        service.add(user);

        verify(repo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void add_whenBodyInvalid_throwExc() {
        when(repo.findOne(user.getLogin())).thenReturn(null);
        User body = new User(
                user.getLogin(),
                user.getName(),
                "",
                user.getSex()
        );
        exception.expect(InvalidUserException.class);

        service.add(body);

        verifyZeroInteractions(repo);
    }

    @Test
    public void edit_successful() {
        when(repo.findOne(user.getLogin())).thenReturn(user);
        User body = new User(
                user.getLogin(),
                "Janusz Cyps",
                "janusz@janusz.pl",
                Sex.MALE
        );

        service.edit(user.getLogin(), body);

        verify(repo, times(1)).findOne(user.getLogin());
        verify(repo, times(1)).save(user);
        verifyNoMoreInteractions(repo);
        assertEquals(body.getMail(), user.getMail());
    }

    @Test
    public void edit_whenUserNotExists_ThrowExc() {
        when(repo.findOne(user.getLogin())).thenReturn(null);
        User body = new User(
                user.getLogin(),
                "Janusz Cyps",
                "janusz@janusz.pl",
                Sex.MALE
        );
        exception.expect(UserNotFoundException.class);

        service.edit(user.getLogin(), body);

        verify(repo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(repo);
        assertNotEquals(body.getMail(), user.getMail());
    }

    @Test
    public void edit_whenBodyInvalid_ThrowExc() {
        when(repo.findOne(user.getLogin())).thenReturn(user);
        User body = new User(
                user.getLogin(),
                user.getName(),
                null,
                user.getSex()
        );
        exception.expect(InvalidUserException.class);

        service.edit(user.getLogin(), body);

        verify(repo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void delete_successful() {
        when(repo.findOne(user.getLogin())).thenReturn(user);

        service.remove(user.getLogin());

        verify(repo, times(1)).findOne(user.getLogin());
        verify(repo, times(1)).delete(user.getLogin());
        verifyNoMoreInteractions(repo);
    }

    @Test
    public void delete_whenUserNotExists_ThrowExc() {
        when(repo.findOne(user.getLogin())).thenReturn(null);
        exception.expect(UserNotFoundException.class);

        service.remove(user.getLogin());

        verify(repo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(repo);
    }
}
