package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.RatingDTO;
import com.tomaszstankowski.movieservice.model.dto.UserDTO;
import com.tomaszstankowski.movieservice.model.entity.Rating;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.enums.UserRole;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.not_found.FollowerNotFoundException;
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
import java.security.Principal;
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
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(path = "/{login}")
    public UserDTO getUser(@PathVariable String login) {
        User user = service.findUser(login);
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
            result = service.findAllUsers(page, sort);
        else
            result = service.findUsersByName(name, page, sort);

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
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO body) {
        User user = service.addUser(mapper.fromDTO(body));
        URI location = ServletUriComponentsBuilder
                .fromPath("/users/{login}")
                .buildAndExpand(user.getLogin())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(mapper.fromEntity(user));
    }

    @PutMapping(path = "/{login}")
    @PreAuthorize("(hasRole('ROLE_USER') AND principal.username == #login) OR hasRole('ROLE_ADMIN')")
    public UserDTO editUser(@PathVariable String login, @RequestBody UserDTO body) {
        body.setLogin(login);
        User user = service.editUser(mapper.fromDTO(body));
        return mapper.fromEntity(user);
    }

    @PutMapping(path = "/{login}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void changeRole(@PathVariable String login, @RequestParam("role") UserRole role) {
        service.changeUserRole(login, role);
    }

    @DeleteMapping(path = "/{login}")
    @PreAuthorize("(hasRole('ROLE_USER') AND principal.username == #login) OR hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String login) {
        service.removeUser(login);
    }

    @GetMapping(path = "/{login}/followers")
    public List<UserDTO> getFollowers(@PathVariable String login) {
        return service.getUserFollowers(login).stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{login}/followed")
    public List<UserDTO> getFollowed(@PathVariable String login) {
        return service.getUserFollowed(login).stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{login}/followers/{follower}")
    public UserDTO getFollower(@PathVariable String login, @PathVariable String follower) {
        User user = service.getUserFollower(login, follower);
        if (user == null)
            throw new FollowerNotFoundException(login, follower);
        return mapper.fromEntity(user);
    }

    @PostMapping(path = "/{login}/followers")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MOD')")
    public ResponseEntity<UserDTO> followUser(@PathVariable String login, Principal principal) {
        User follower = service.addUserFollower(login, principal.getName());
        URI location = ServletUriComponentsBuilder
                .fromPath("/users/{login}/followers/{follower}")
                .buildAndExpand(login, follower.getLogin())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(mapper.fromEntity(follower));
    }

    @DeleteMapping(path = "/{login}/followers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MOD')")
    public void unfollowUser(@PathVariable String login, Principal principal) {
        service.removeUserFollower(login, principal.getName());
    }

    @GetMapping(path = "/{login}/followed/ratings")
    @PreAuthorize("(hasRole('ROLE_USER') AND principal.username == #login) OR hasRole('ROLE_ADMIN')")
    public List<RatingDTO> getUserFollowedRatings(@PathVariable String login,
                                                  @RequestParam(value = "page", required = false) Integer page,
                                                  @RequestParam(value = "show", required = false) Long showId) {
        List<Rating> ratings;
        if (showId == null) {
            if (page == null)
                page = 0;
            Page<Rating> pages = service.findUserFollowersRatings(login, page);
            if (page >= pages.getTotalPages() && page > 0)
                throw new PageNotFoundException(page);
            ratings = pages.getContent();
        } else {
            ratings = service.findUserFollowersRatings(login, showId);
        }
        return ratings
                .stream()
                .map(mapper::fromEntity)
                .collect(Collectors.toList());
    }


}
