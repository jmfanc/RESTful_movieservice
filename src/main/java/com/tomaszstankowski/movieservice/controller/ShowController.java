package com.tomaszstankowski.movieservice.controller;

import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.ParticipationDTO;
import com.tomaszstankowski.movieservice.model.dto.ShowDTO;
import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.ShowType;
import com.tomaszstankowski.movieservice.repository.specifications.MovieSpecifications;
import com.tomaszstankowski.movieservice.repository.specifications.SerialSpecifications;
import com.tomaszstankowski.movieservice.repository.specifications.ShowSpecifications;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.not_found.PageNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ShowNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specifications.where;

@RestController
@RequestMapping("/shows")
@EnableSpringDataWebSupport
public class ShowController {

    private final ShowService service;
    private final ModelMapper mapper;

    public ShowController(ShowService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ShowDTO> getShows(@RequestParam("page") int page,
                                  @RequestParam(value = "type", required = false) ShowType type,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(value = "year_lt", required = false) Integer yearLt,
                                  @RequestParam(value = "year_gt", required = false) Integer yearGt,
                                  @RequestParam(value = "genres", required = false) String[] genres,
                                  @RequestParam(value = "duration_lt", required = false) Integer durationLt,
                                  @RequestParam(value = "duration_gt", required = false) Integer durationGt,
                                  @RequestParam(value = "seasons_lt", required = false) Integer seasonsLt,
                                  @RequestParam(value = "seasons_gt", required = false) Integer seasonsGt,
                                  @SortDefault("title") Sort sort) {
        if (type == ShowType.MOVIE)
            return getMovies(page, title, yearLt, yearGt, genres, durationLt, durationGt, sort);
        if (type == ShowType.SERIAL)
            return getSeries(page, title, yearLt, yearGt, genres, seasonsLt, seasonsGt, sort);


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

        Page<Show> result = service.findShows(specs, page, sort);
        validate(result);

        return result.getContent().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private List<ShowDTO> getMovies(int page,
                                    String title,
                                    Integer yearLt,
                                    Integer yearGt,
                                    String[] genres,
                                    Integer durationLt,
                                    Integer durationGt,
                                    Sort sort) {

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
        validate(result);

        return result.getContent().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private List<ShowDTO> getSeries(int page,
                                    String title,
                                    Integer yearLt,
                                    Integer yearGt,
                                    String[] genres,
                                    Integer seasonsLt,
                                    Integer seasonsGt,
                                    Sort sort) {

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
        validate(result);

        return result.getContent().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ShowDTO getShow(@PathVariable("id") long id) {
        Show show = service.findShow(id);
        if (show == null)
            throw new ShowNotFoundException(id);
        return map(show);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addShow(@RequestBody ShowDTO body) {
        Show show = service.addShow(mapper.fromDTO(body));
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/{id}")
                .buildAndExpand(show.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void editShow(@PathVariable("id") long id, @RequestBody ShowDTO body) {
        service.editShow(id, mapper.fromDTO(body));
    }

    @DeleteMapping(path = "/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteShow(@PathVariable("id") long id) {
        service.removeShow(id);
    }

    @GetMapping(path = "/genres")
    public List<String> getGenres() {
        return service.findAllGenres()
                .stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/{id}/participations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addParticipation(@PathVariable("id") long showId,
                                              @RequestParam("person") long personId,
                                              @RequestBody ParticipationDTO body) {
        Participation participation = service.addParticipation(showId, personId, mapper.fromDTO(body));
        URI location = ServletUriComponentsBuilder
                .fromPath("/shows/{showId}/participations/{participationId}")
                .buildAndExpand(showId, participation.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(path = "/{showId}/participations/{participationId}/edit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void editParticipation(@PathVariable("showId") long showId,
                                  @PathVariable("participationId") long participationId,
                                  @RequestBody ParticipationDTO body) {
        Show show = service.findShow(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        service.editParticipation(participationId, mapper.fromDTO(body));
    }

    @DeleteMapping(path = "/{showId}/participations/{participationId}/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteParticipation(@PathVariable("showId") long showId,
                                    @PathVariable("participationId") long participationId) {
        Show show = service.findShow(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        service.removeParticipation(participationId);
    }

    @GetMapping(path = "/{id}/participations")
    public List<ParticipationDTO> getParticipations(@PathVariable("id") long id,
                                                    @RequestParam(value = "role", required = false) Profession role) {
        return service.findParticipations(id, role)
                .stream()
                .map(entity -> {
                    ParticipationDTO dto = mapper.fromEntity(entity);
                    long showId = dto.getShow().getId();
                    dto.getShow().setRateCount(service.getShowRateCount(showId));
                    dto.getShow().setRating(service.getShowRating(showId));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/{id}/rate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void rateShow(@PathVariable("id") long id,
                         @RequestParam("login") String login,
                         @RequestParam("rating") short rating) {
        service.rate(id, login, rating);
    }

    @DeleteMapping(path = "/{id}/rate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteShowRating(@PathVariable("id") long id,
                                 @RequestParam("login") String login) {
        service.removeRating(id, login);
    }

    private ShowDTO map(Show entity) {
        ShowDTO dto = mapper.fromEntity(entity);
        dto.setRateCount(service.getShowRateCount(entity.getId()));
        dto.setRating(service.getShowRating(entity.getId()));
        return dto;
    }

    private void validate(Page page) {
        if (page.getNumber() >= page.getTotalPages() && page.getNumber() > 0)
            throw new PageNotFoundException(page.getNumber());
    }
}
