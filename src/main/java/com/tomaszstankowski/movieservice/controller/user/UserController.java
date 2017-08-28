package com.tomaszstankowski.movieservice.controller.user;

import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final UserRepository repository;

    public UserController(final UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/{login}")
    public User getUserByLogin(@PathVariable String login) {
        User user = repository.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        return user;
    }

    @PostMapping(path = "/add")
    ResponseEntity<?> addUser(@RequestBody User body) {
        validateUser(body);
        if (repository.findOne(body.getLogin()) != null)
            throw new UserAlreadyExistsException(body.getLogin());
        User user = new User(
                body.getLogin(),
                body.getName(),
                body.getMail(),
                body.getSex()
        );
        repository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromPath("/user/{login}")
                .buildAndExpand(user.getLogin()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{login}/edit")
    ResponseEntity<?> editUser(@PathVariable String login, @RequestBody User body) {
        User user = repository.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        user.setName(body.getName());
        user.setMail(body.getMail());
        user.setSex(body.getSex());
        repository.save(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(path = "/{login}/delete")
    ResponseEntity<?> deleteUser(@PathVariable String login) {
        User user = repository.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        repository.delete(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private void validateUser(User user) {
        if (user.getLogin() == null
                || user.getLogin().isEmpty()
                || user.getMail() == null
                || user.getMail().isEmpty())
            throw new InvalidUserException();
    }
}
