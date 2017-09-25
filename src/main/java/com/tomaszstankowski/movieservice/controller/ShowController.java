package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.tomaszstankowski.movieservice.repository.specifications.ShowSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@RestController
@RequestMapping("/shows")
@EnableSpringDataWebSupport
public class ShowController {

    private final ShowService service;

    public ShowController(final ShowService service) {
        this.service = service;
    }

    @GetMapping
    public List<Show> getShows(@RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "year_lt", required = false) Integer yearLt,
                               @RequestParam(value = "year_gt", required = false) Integer yearGt,
                               @RequestParam(value = "genres[]", required = false) String[] genres,
                               @SortDefault("title") Sort sort) {
        Specifications<Show> specs = null;
        if (title != null)
            specs = where(titleContains(title));
        if (yearLt != null)
            specs = (specs == null) ? where(olderThan(yearLt)) : specs.and(olderThan(yearLt));
        if (yearGt != null)
            specs = (specs == null) ? where(youngerThan(yearGt)) : specs.and(youngerThan(yearGt));
        return service.findAll(specs, sort);
    }

    @GetMapping(path = "/movies/{id}")
    public Movie getMovie(@PathVariable("id") long id) {
        Movie movie = service.findMovie(id);
        if (movie == null)
            throw new ShowNotFoundException(id);
        return movie;
    }

    @GetMapping(path = "/series/{id}")
    public Serial getSerial(@PathVariable("id") long id) {
        Serial serial = service.findSerial(id);
        if (serial == null)
            throw new ShowNotFoundException(id);
        return serial;
    }

    @PostMapping(path = "/movies/add")
    public ResponseEntity<?> addMovie(@RequestBody Movie body) {
        service.addMovie(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/movies/{id}")
                .buildAndExpand(body.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/series/add")
    public ResponseEntity<?> addSerial(@RequestBody Serial body) {
        service.addSerial(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/series/{id}")
                .buildAndExpand(body.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/movies/{id}/edit")
    public ResponseEntity<?> editMovie(@PathVariable("id") long id, @RequestBody Movie body) {
        service.editMovie(id, body);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/series/{id}/edit")
    public ResponseEntity<?> editSerial(@PathVariable("id") long id, @RequestBody Serial body) {
        service.editSerial(id, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/movies/{id}/delete")
    public ResponseEntity<?> deleteMovie(@PathVariable("id") long id) {
        service.removeMovie(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/series/{id}/delete")
    public ResponseEntity<?> deleteSerial(@PathVariable("id") long id) {
        service.removeSerial(id);
        return ResponseEntity.ok().build();
    }
}
