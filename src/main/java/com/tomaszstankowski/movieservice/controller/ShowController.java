package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.service.ShowService;
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

    private final ShowService showService;

    public ShowController(final ShowService showService) {
        this.showService = showService;
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
        return showService.findAll(specs, sort);
    }

    @PostMapping(path = "/movies/add")
    public ResponseEntity<?> addMovie(@RequestBody Movie body) {
        showService.addShow(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/movies/{id}")
                .buildAndExpand(body.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/series/add")
    public ResponseEntity<?> addSerial(@RequestBody Serial body) {
        showService.addShow(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/series/{id}")
                .buildAndExpand(body.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
