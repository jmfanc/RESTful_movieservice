package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.model.dto.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.UserDTO;
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
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/{login}")
    public UserDTO getUser(@PathVariable String login) {
        User user = userService.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        return modelMapper.fromEntity(user);
    }

    @GetMapping
    public List<UserDTO> getUsers(@RequestParam("page") int page,
                                  @RequestParam(value = "name", required = false) String name,
                                  @SortDefault("login") Sort sort) {
        Page<User> result;

        if (name == null)
            result = userService.findAll(page, sort);
        else
            result = userService.findByName(name, page, sort);

        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);

        return result.getContent().stream()
                .map(modelMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addUser(@RequestBody UserDTO body) {
        User user = modelMapper.fromDTO(body);
        userService.add(user);
        URI location = ServletUriComponentsBuilder
                .fromPath("/users/{login}")
                .buildAndExpand(body.getLogin())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{login}/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable String login, @RequestBody UserDTO body) {
        User user = modelMapper.fromDTO(body);
        userService.edit(login, user);
    }

    @DeleteMapping(path = "/{login}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String login) {
        userService.remove(login);
    }
}
