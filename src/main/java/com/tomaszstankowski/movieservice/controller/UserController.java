package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.UserNotFoundException;
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
@RequestMapping(path = "/users")
@EnableSpringDataWebSupport
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{login}")
    public ResponseEntity<User> getUser(@PathVariable String login) {
        User user = userService.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam("page") int page,
                                               @RequestParam(value = "name", required = false) String name,
                                               @SortDefault("login") Sort sort) {
        Page<User> result;

        if (name == null)
            result = userService.findAll(page, sort);
        else
            result = userService.findByName(name, page, sort);

        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return ResponseEntity.ok(result.getContent());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addUser(@RequestBody User body) {
        userService.add(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/users/{login}")
                .buildAndExpand(body.getLogin())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{login}/edit")
    public ResponseEntity<?> editUser(@PathVariable String login, @RequestBody User body) {
        userService.edit(login, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{login}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String login) {
        userService.remove(login);
        return ResponseEntity.ok().build();
    }
}
