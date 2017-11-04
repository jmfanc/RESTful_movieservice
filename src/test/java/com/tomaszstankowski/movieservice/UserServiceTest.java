package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.repository.RatingRepository;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.conflict.EmailAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.conflict.UserAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidUserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private RatingRepository ratingRepo;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private User user;

    @Before
    public void setup() {
        service = new UserService(userRepo, ratingRepo, encoder);
        user = new User(
                "janusz111",
                "password",
                "Janusz Cyps",
                "jan@usz.pl",
                Sex.MALE
        );
    }

    @Test
    public void add_successful() {
        when(userRepo.findOne(user.getLogin())).thenReturn(null);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);

        service.add(user);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(userRepo, times(1)).findByEmail(user.getEmail());
        verify(userRepo, times(1)).save(user);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void add_whenUserAlreadyExists_throwExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        exception.expect(UserAlreadyExistsException.class);

        service.add(user);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void add_whenUserWithEmailExists_throwExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(null);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        exception.expect(EmailAlreadyExistsException.class);

        service.add(user);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(userRepo, times(1)).findByEmail(user.getEmail());
        ;
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void add_whenBodyInvalid_throwExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(null);
        User body = new User(
                user.getLogin(),
                user.getPassword(),
                user.getName(),
                "",
                user.getSex()
        );
        exception.expect(InvalidUserException.class);

        service.add(body);

        verifyZeroInteractions(userRepo);
    }

    @Test
    public void edit_successful() {
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        User body = new User(
                user.getLogin(),
                user.getPassword(),
                "Janusz Cyps",
                "janusz@janusz.pl",
                Sex.MALE
        );
        when(userRepo.findByEmail(body.getEmail())).thenReturn(null);

        service.edit(body);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(userRepo, times(1)).findByEmail(user.getEmail());
        verify(userRepo, times(1)).save(user);
        verifyNoMoreInteractions(userRepo);
        assertEquals(body.getEmail(), user.getEmail());
    }

    @Test
    public void edit_whenUserNotExists_ThrowExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(null);
        User body = new User(
                user.getLogin(),
                user.getPassword(),
                "Janusz Cyps",
                "janusz@janusz.pl",
                Sex.MALE
        );
        exception.expect(UserNotFoundException.class);

        service.edit(body);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(userRepo);
        assertNotEquals(body.getEmail(), user.getEmail());
    }

    @Test
    public void edit_whenBodyInvalid_ThrowExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        User body = new User(
                user.getLogin(),
                user.getPassword(),
                user.getName(),
                null,
                user.getSex()
        );
        exception.expect(InvalidUserException.class);

        service.edit(body);

        verify(userRepo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void delete_successful() {
        when(userRepo.findOne(user.getLogin())).thenReturn(user);

        service.remove(user.getLogin());

        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(userRepo, times(1)).delete(user.getLogin());
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void delete_whenUserNotExists_ThrowExc() {
        when(userRepo.findOne(user.getLogin())).thenReturn(null);
        exception.expect(UserNotFoundException.class);

        service.remove(user.getLogin());

        verify(userRepo, times(1)).findOne(user.getLogin());
        verifyNoMoreInteractions(userRepo);
    }
}
