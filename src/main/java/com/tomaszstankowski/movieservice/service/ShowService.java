package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.Genre;
import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.repository.GenreRepository;
import com.tomaszstankowski.movieservice.repository.MovieRepository;
import com.tomaszstankowski.movieservice.repository.SerialRepository;
import com.tomaszstankowski.movieservice.repository.ShowRepository;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class ShowService {

    private final ShowRepository showRepo;
    private final MovieRepository movieRepo;
    private final SerialRepository serialRepo;
    private final GenreRepository genreRepo;

    public ShowService(ShowRepository showRepo, MovieRepository movieRepo, SerialRepository serialRepo, GenreRepository genreRepo) {
        this.showRepo = showRepo;
        this.movieRepo = movieRepo;
        this.serialRepo = serialRepo;
        this.genreRepo = genreRepo;
    }

    public Movie findMovie(long id) {
        return movieRepo.findOne(id);
    }

    public Serial findSerial(long id) {
        return serialRepo.findOne(id);
    }

    public Page<Show> findAll(Specification<Show> spec, int page, Sort sort) {
        return showRepo.findAll(spec, createPegeable(page, sort));
    }

    public List<Movie> findMovies(Specification<Movie> spec, Sort sort) {
        return movieRepo.findAll(spec, sort);
    }

    public List<Serial> findSeries(Specification<Serial> spec, Sort sort) {
        return serialRepo.findAll(spec, sort);
    }

    public void addMovie(Movie body) {
        Movie movie = new Movie(
                body.getTitle(),
                body.getDescription(),
                body.getReleaseDate(),
                body.getLocation(),
                body.getDuration(),
                body.getBoxoffice()
        );
        addShow(movie, body);
    }

    public void addSerial(Serial body) {
        Serial serial = new Serial(
                body.getTitle(),
                body.getDescription(),
                body.getReleaseDate(),
                body.getLocation(),
                body.getSeasons()
        );
        addShow(serial, body);
    }

    private void addShow(Show show, Show body) {
        validateShow(show);
        checkIfShowAlreadyExists(show);
        connectShowWithGenres(show, body.getGenres());
        showRepo.save(show);
    }

    private void connectShowWithGenres(Show show, Set<Genre> genres) {
        for (Genre g : genres) {
            Genre genre = genreRepo.findOne(g.getName());
            if (genre == null)
                genre = genreRepo.save(g);
            genre.getShows().add(show);
            show.getGenres().add(genre);
        }
    }

    private void disconnectShowFromGenres(Show show) {
        Iterator<Genre> iterator = show.getGenres().iterator();
        while (iterator.hasNext()) {
            Genre g = iterator.next();
            g.getShows().remove(show);
            iterator.remove();
        }
    }

    public void editMovie(long id, Movie body) {
        Movie movie = movieRepo.findOne(id);
        if (movie == null)
            throw new ShowNotFoundException(id);
        movie.setDuration(body.getDuration());
        movie.setBoxoffice(body.getBoxoffice());
        editShow(movie, body);
    }

    public void editSerial(long id, Serial body) {
        Serial serial = serialRepo.findOne(id);
        if (serial == null)
            throw new ShowNotFoundException(id);
        serial.setSeasons(body.getSeasons());
        editShow(serial, body);
    }

    private void editShow(Show show, Show body) {
        validateShow(body);

        show.setTitle(body.getTitle());
        show.setDescription(body.getDescription());
        show.setLocation(body.getLocation());
        show.setReleaseDate(body.getReleaseDate());

        disconnectShowFromGenres(show);
        connectShowWithGenres(show, body.getGenres());
        showRepo.save(show);
    }

    public void removeMovie(long id) {
        Movie movie = movieRepo.findOne(id);
        if (movie == null)
            throw new ShowNotFoundException(id);
        removeShow(movie);
    }

    public void removeSerial(long id) {
        Serial serial = serialRepo.findOne(id);
        if (serial == null)
            throw new ShowNotFoundException(id);
        removeShow(serial);
    }

    private void removeShow(Show show) {
        disconnectShowFromGenres(show);
        showRepo.delete(show);
    }

    private Pageable createPegeable(int page, Sort sort) {
        return new PageRequest(page, 5, sort);
    }

    private void validateShow(Show show) {
        if (show.getTitle() == null || show.getTitle().isEmpty())
            throw new InvalidShowException();
    }

    private void checkIfShowAlreadyExists(Show show) {
        if (showRepo.findByTitleAndReleaseDate(show.getTitle(), show.getReleaseDate()) != null)
            throw new ShowAlreadyExistsException(show.getTitle(), show.getReleaseDate());
    }
}
