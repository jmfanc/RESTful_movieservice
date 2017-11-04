package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.RatingDTO;
import com.tomaszstankowski.movieservice.model.dto.UserDTO;
import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.not_found.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tomaszstankowski.movieservice.repository.specifications.RatingSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@RestController
@RequestMapping(path = "/users")
@EnableSpringDataWebSupport
public class UserController {
    private final UserService service;
    private final ShowService showService;
    private final ModelMapper mapper;

    public UserController(UserService service, ShowService showService, ModelMapper mapper) {
        this.service = service;
        this.showService = showService;
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

    @GetMapping(path = "/{login}/ratings")
    public List<RatingDTO> getUserRatings(@PathVariable("login") String login,
                                          @RequestParam("page") int page,
                                          @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                          @RequestParam(value = "rating", required = false) Short rating) {
        Specifications<Rating> specs = where(user(login));
        if (from != null)
            specs = specs.and(youngerThan(from));
        if (rating != null)
            specs = specs.and(rating(rating));

        Page<Rating> result = service.findUserRatings(specs, page);
        if (page >= result.getTotalPages() && page > 0)
            throw new PageNotFoundException(page);
        return result.getContent()
                .stream()
                .map(entity -> {
                    RatingDTO dto = mapper.fromEntity(entity);
                    long showId = dto.getShow().getId();
                    dto.getShow().setRating(showService.getShowRating(showId));
                    dto.getShow().setRateCount(showService.getShowRateCount(showId));
                    return dto;
                })
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
    @PreAuthorize("(hasRole('ROLE_USER') AND principal.username == #login) OR hasRole('ROLE_ADMIN')")
    public void editUser(@PathVariable String login, @RequestBody UserDTO body) {
        body.setLogin(login);
        User user = mapper.fromDTO(body);
        service.edit(user);
    }

    @DeleteMapping(path = "/{login}/delete")
    @PreAuthorize("(hasRole('ROLE_USER') AND principal.username == #login) OR hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String login) {
        service.remove(login);
    }
}
