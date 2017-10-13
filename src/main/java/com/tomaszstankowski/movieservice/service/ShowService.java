package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.repository.*;
import com.tomaszstankowski.movieservice.service.exception.already_exists.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidRatingException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.UnexpectedShowException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.UnknownTypeException;
import com.tomaszstankowski.movieservice.service.exception.not_found.*;
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
    private final UserRepository userRepo;
    private final ParticipationRepository participationRepo;
    private final RatingRepository ratingRepo;

    public ShowService(ShowRepository showRepo,
                       MovieRepository movieRepo,
                       SerialRepository serialRepo,
                       GenreRepository genreRepo,
                       PersonRepository personRepo,
                       UserRepository userRepo,
                       ParticipationRepository participationRepo,
                       RatingRepository ratingRepo) {
        this.showRepo = showRepo;
        this.movieRepo = movieRepo;
        this.serialRepo = serialRepo;
        this.genreRepo = genreRepo;
        this.personRepo = personRepo;
        this.participationRepo = participationRepo;
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
    }

    public Show findShow(long id) {
        return showRepo.findOne(id);
    }

    public Page<Show> findShows(Specification<Show> spec, int page, Sort sort) {
        return showRepo.findAll(spec, createPageable(page, sort));
    }

    public Page<Movie> findMovies(Specification<Movie> spec, int page, Sort sort) {
        return movieRepo.findAll(spec, createPageable(page, sort));
    }

    public Page<Serial> findSeries(Specification<Serial> spec, int page, Sort sort) {
        return serialRepo.findAll(spec, createPageable(page, sort));
    }

    public List<Participation> findParticipations(long id, Profession role) {
        Show show = showRepo.findOne(id);
        if (show == null)
            throw new ShowNotFoundException(id);
        if (role == null)
            return show.getParticipations();
        return participationRepo.findByShowAndRole(show, role);
    }

    public List<Genre> findAllGenres() {
        return genreRepo.findAll(new Sort("name"));
    }

    public Show addShow(Show show) {
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

    public Participation addParticipation(long showId, long personId, Participation participation) {
        Person person = personRepo.findOne(personId);
        if (person == null)
            throw new PersonNotFoundException(personId);
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);

        participation.setShow(show);
        participation.setPerson(person);
        show.getParticipations().add(participation);
        person.getParticipations().add(participation);
        return participationRepo.save(participation);
    }

    public void editParticipation(long id, Participation body) {
        Participation participation = participationRepo.findOne(id);
        if (participation == null)
            throw new ParticipationNotFoundException(id);
        participation.setRole(body.getRole());
        participation.setInfo(body.getInfo());
        participationRepo.save(participation);
    }

    public void removeParticipation(long id) {
        Participation participation = participationRepo.findOne(id);
        if (participation == null)
            throw new ParticipationNotFoundException(id);
        participationRepo.delete(id);
    }

    public float getShowRating(long showId) {
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        Float result = ratingRepo.getAverageRatingForShow(showId);
        return result == null ? 0f : result;
    }

    public long getShowRateCount(long showId) {
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        return ratingRepo.countByShow(show);
    }

    public Rating rate(long showId, String login, short rating) {
        validateRating(rating);
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        Rating oldRating = ratingRepo.findByUserAndShow(user, show);
        if (oldRating != null) {
            if (oldRating.getRating() == rating)
                return oldRating;
            ratingRepo.delete(oldRating);
        }
        Rating newRating = new Rating(rating, show, user);
        show.getRatings().add(newRating);
        user.getRatings().add(newRating);
        return ratingRepo.save(newRating);
    }

    public void removeRating(long showId, String login) {
        Show show = showRepo.findOne(showId);
        if (show == null)
            throw new ShowNotFoundException(showId);
        User user = userRepo.findOne(login);
        if (user == null)
            throw new UserNotFoundException(login);
        Rating rating = ratingRepo.findByUserAndShow(user, show);
        if (rating == null)
            throw new RatingNotFoundException(show, user);
        ratingRepo.delete(rating);
    }

    public void editShow(long id, Show body) {
        validateShow(body);
        Show show = showRepo.findOne(id);
        if (show == null)
            throw new ShowNotFoundException(id);
        show.setTitle(body.getTitle());
        show.setDescription(body.getDescription());
        show.setLocation(body.getLocation());
        show.setReleaseDate(body.getReleaseDate());

        boolean isOk = false;
        if (body instanceof Movie)
            if (show instanceof Movie) {
                editMovie((Movie) show, (Movie) body);
                isOk = true;
            } else
                throw new UnexpectedShowException(Movie.class, show.getClass());
        if (body instanceof Serial)
            if (show instanceof Serial) {
                editSerial((Serial) show, (Serial) body);
                isOk = true;
            } else
                throw new UnexpectedShowException(Serial.class, show.getClass());
        if (isOk) {
            disconnectShowFromGenres(show);
            show.getGenres().addAll(body.getGenres());
            connectShowWithGenres(show);
            showRepo.save(show);
        } else {
            throw new UnknownTypeException(Show.class);
        }
    }

    private void editMovie(Movie movie, Movie body) {
        movie.setDuration(body.getDuration());
        movie.setBoxoffice(body.getBoxoffice());
    }

    private void editSerial(Serial serial, Serial body) {
        serial.setSeasons(body.getSeasons());
    }

    public void removeShow(long id) {
        Show show = showRepo.findOne(id);
        if (show == null)
            throw new ShowNotFoundException(id);
        disconnectShowFromGenres(show);
        showRepo.delete(show);
    }

    private Pageable createPageable(int page, Sort sort) {
        return new PageRequest(page, 5, sort);
    }

    private void validateRating(short rating) {
        if (rating > 10 || rating < 1)
            throw new InvalidRatingException(rating);
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
