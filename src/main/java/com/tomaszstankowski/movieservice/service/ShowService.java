package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.repository.*;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
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

@Service
public class ShowService {

    private final ShowRepository showRepo;
    private final MovieRepository movieRepo;
    private final SerialRepository serialRepo;
    private final GenreRepository genreRepo;
    private final PersonRepository personRepo;
    private final ParticipationRepository participationRepo;

    public ShowService(ShowRepository showRepo,
                       MovieRepository movieRepo,
                       SerialRepository serialRepo,
                       GenreRepository genreRepo,
                       PersonRepository personRepo,
                       ParticipationRepository participationRepo) {
        this.showRepo = showRepo;
        this.movieRepo = movieRepo;
        this.serialRepo = serialRepo;
        this.genreRepo = genreRepo;
        this.personRepo = personRepo;
        this.participationRepo = participationRepo;
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

    public Page<Movie> findMovies(Specification<Movie> spec, int page, Sort sort) {
        return movieRepo.findAll(spec, createPegeable(page, sort));
    }

    public Page<Serial> findSeries(Specification<Serial> spec, int page, Sort sort) {
        return serialRepo.findAll(spec, createPegeable(page, sort));
    }

    public List<Participation> findMovieParticipations(long id, Person.Profession role) {
        Movie movie = movieRepo.findOne(id);
        if (movie == null)
            throw new ShowNotFoundException(id);
        return findParticipations(movie, role);
    }

    public List<Participation> findSerialParticipations(long id, Person.Profession role) {
        Serial serial = serialRepo.findOne(id);
        if (serial == null)
            throw new ShowNotFoundException(id);
        return findParticipations(serial, role);
    }

    private List<Participation> findParticipations(Show show, Person.Profession role) {
        if (role == null)
            return show.getParticipations();
        return participationRepo.findByShowAndRole(show, role);
    }

    public Movie addMovie(Movie movie) {
        return (Movie) addShow(movie);
    }

    public Serial addSerial(Serial serial) {
        return (Serial) addShow(serial);
    }

    private Show addShow(Show show) {
        validateShow(show);
        checkIfShowAlreadyExists(show);
        connectShowWithGenres(show);
        return showRepo.save(show);
    }

    private void connectShowWithGenres(Show show) {
        for (Genre g : show.getGenres()) {
            Genre genre = genreRepo.findOne(g.getName());
            if (genre == null)
                genre = genreRepo.save(g);
            genre.getShows().add(show);
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

    public void addParticipation(long personId, long showId, Participation participation) {
        Person person = personRepo.findOne(personId);
        if (person == null)
            throw new PersonNotFoundException(personId);
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);

        participation.setShow(show);
        participation.setPerson(person);
        show.getParticipations().add(participation);
        showRepo.save(show);
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
        show.getGenres().addAll(body.getGenres());
        connectShowWithGenres(show);
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
