package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.UserDTO;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.UserNotFoundException;
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
@RequestMapping(path = "/users")
@EnableSpringDataWebSupport
public class UserController {
    private final UserService service;
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(path = "/{login}")
    public UserDTO getUser(@PathVariable String login) {
        User user = service.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        return mapper.fromEntity(user);
    }

    @GetMapping
    public List<UserDTO> getUsers(@RequestParam("page") int page,
                                  @RequestParam(value = "name", required = false) String name,
                                  @SortDefault("login") Sort sort) {
        Page<User> result;

        if (name == null)
            result = service.findAll(page, sort);
        else
            result = service.findByName(name, page, sort);

        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);

        return result.getContent().stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addUser(@RequestBody UserDTO body) {
        User user = mapper.fromDTO(body);
        service.add(user);
        URI location = ServletUriComponentsBuilder
                .fromPath("/users/{login}")
                .buildAndExpand(body.getLogin())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{login}/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable String login, @RequestBody UserDTO body) {
        User user = mapper.fromDTO(body);
        service.edit(login, user);
    }

    @DeleteMapping(path = "/{login}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String login) {
        service.remove(login);
    }
}
