package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.repository.specifications.MovieSpecifications;
import com.tomaszstankowski.movieservice.repository.specifications.SerialSpecifications;
import com.tomaszstankowski.movieservice.repository.specifications.ShowSpecifications;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public List<Show> getShows(@RequestParam("page") int page,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "year_lt", required = false) Integer yearLt,
                               @RequestParam(value = "year_gt", required = false) Integer yearGt,
                               @RequestParam(value = "genres", required = false) String[] genres,
                               @SortDefault("title") Sort sort) {

        Specifications<Show> specs = null;
        if (title != null)
            specs = where(ShowSpecifications.titleContains(title));
        if (yearLt != null)
            specs = (specs == null) ? where(ShowSpecifications.olderThan(yearLt))
                    : specs.and(ShowSpecifications.olderThan(yearLt));
        if (yearGt != null)
            specs = (specs == null) ? where(ShowSpecifications.youngerThan(yearGt))
                    : specs.and(ShowSpecifications.youngerThan(yearGt));
        if (genres != null)
            specs = (specs == null) ? where(ShowSpecifications.hasAtLeastOneGenre(genres))
                    : specs.and(ShowSpecifications.hasAtLeastOneGenre(genres));

        Page<Show> result = service.findAll(specs, page, sort);
        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return result.getContent();
    }

    @GetMapping(path = "/movies")
    public List<Movie> getMovies(@RequestParam("page") int page,
                                 @RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "year_lt", required = false) Integer yearLt,
                                 @RequestParam(value = "year_gt", required = false) Integer yearGt,
                                 @RequestParam(value = "genres", required = false) String[] genres,
                                 @RequestParam(value = "duration_lt", required = false) Integer durationLt,
                                 @RequestParam(value = "duration_gt", required = false) Integer durationGt,
                                 @SortDefault("title") Sort sort) {

        Specifications<Movie> specs = null;
        if (title != null)
            specs = where(MovieSpecifications.titleContains(title));
        if (yearLt != null)
            specs = (specs == null) ? where(MovieSpecifications.olderThan(yearLt))
                    : specs.and(MovieSpecifications.olderThan(yearLt));
        if (yearGt != null)
            specs = (specs == null) ? where(MovieSpecifications.youngerThan(yearGt))
                    : specs.and(MovieSpecifications.youngerThan(yearGt));
        if (genres != null)
            specs = (specs == null) ? where(MovieSpecifications.hasAtLeastOneGenre(genres))
                    : specs.and(MovieSpecifications.hasAtLeastOneGenre(genres));
        if (durationLt != null)
            specs = (specs == null) ? where(MovieSpecifications.shorterThan(durationLt))
                    : specs.and(MovieSpecifications.shorterThan(durationLt));
        if (durationGt != null)
            specs = (specs == null) ? where(MovieSpecifications.longerThan(durationGt))
                    : specs.and(MovieSpecifications.longerThan(durationGt));

        Page<Movie> result = service.findMovies(specs, page, sort);
        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return result.getContent();
    }

    @GetMapping(path = "/series")
    public List<Serial> getSeries(@RequestParam("page") int page,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(value = "year_lt", required = false) Integer yearLt,
                                  @RequestParam(value = "year_gt", required = false) Integer yearGt,
                                  @RequestParam(value = "genres", required = false) String[] genres,
                                  @RequestParam(value = "seasons_lt", required = false) Integer seasonsLt,
                                  @RequestParam(value = "seasons_gt", required = false) Integer seasonsGt,
                                  @SortDefault("title") Sort sort) {

        Specifications<Serial> specs = null;
        if (title != null)
            specs = where(SerialSpecifications.titleContains(title));
        if (yearLt != null)
            specs = (specs == null) ? where(SerialSpecifications.olderThan(yearLt))
                    : specs.and(SerialSpecifications.olderThan(yearLt));
        if (yearGt != null)
            specs = (specs == null) ? where(SerialSpecifications.youngerThan(yearGt))
                    : specs.and(SerialSpecifications.youngerThan(yearGt));
        if (genres != null)
            specs = (specs == null) ? where(SerialSpecifications.hasAtLeastOneGenre(genres))
                    : specs.and(SerialSpecifications.hasAtLeastOneGenre(genres));
        if (seasonsLt != null)
            specs = (specs == null) ? where(SerialSpecifications.shorterThan(seasonsLt))
                    : specs.and(SerialSpecifications.shorterThan(seasonsLt));
        if (seasonsGt != null)
            specs = (specs == null) ? where(SerialSpecifications.longerThan(seasonsGt))
                    : specs.and(SerialSpecifications.longerThan(seasonsGt));

        Page<Serial> result = service.findSeries(specs, page, sort);
        if (page >= result.getTotalPages())
            throw new PageNotFoundException(page);
        return result.getContent();
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
        Movie movie = service.addMovie(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/movies/{id}")
                .buildAndExpand(movie.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping(path = "/series/add")
    public ResponseEntity<?> addSerial(@RequestBody Serial body) {
        Serial serial = service.addSerial(body);
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/series/{id}")
                .buildAndExpand(serial.getId())
                .toUri();
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
