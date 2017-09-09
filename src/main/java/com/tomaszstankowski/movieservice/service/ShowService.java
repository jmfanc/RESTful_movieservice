package com.tomaszstankowski.movieservice.service;

import com.tomaszstankowski.movieservice.model.Genre;
import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.repository.GenreRepository;
import com.tomaszstankowski.movieservice.repository.MovieRepository;
import com.tomaszstankowski.movieservice.repository.ShowRepository;
import com.tomaszstankowski.movieservice.repository.specifications.SerialRepository;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.ShowAlreadyExistsException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Show> findAll(Specification<Show> spec, Sort sort) {
        return showRepo.findAll(spec, sort);
    }

    public List<Movie> findMovies(Specification<Movie> spec, Sort sort) {
        return movieRepo.findAll(spec, sort);
    }

    public List<Serial> findSeries(Specification<Serial> spec, Sort sort) {
        return serialRepo.findAll(spec, sort);
    }

    public void addShow(Show body) {
        validateShow(body);
        Show show;
        if (body instanceof Movie) {
            Movie m = (Movie) body;
            show = new Movie(
                    m.getTitle(),
                    m.getDescription(),
                    m.getReleaseDate(),
                    m.getLocation(),
                    m.getDuration(),
                    m.getBoxoffice()
            );
        } else {
            Serial s = (Serial) body;
            show = new Serial(
                    s.getTitle(),
                    s.getDescription(),
                    s.getReleaseDate(),
                    s.getLocation(),
                    s.getSeasons()
            );
        }
        for (Genre g : body.getGenres()) {
            Genre genre = genreRepo.findOne(g.getName());
            if (genre == null)
                genre = genreRepo.save(g);
            show.addGenre(genre);
        }
        showRepo.save(show);
    }

    private void validateShow(Show show) {
        if (show.getTitle() == null || show.getTitle().isEmpty())
            throw new InvalidShowException();
        if (showRepo.findByTitleAndReleaseDate(show.getTitle(), show.getReleaseDate()) != null)
            throw new ShowAlreadyExistsException(show.getTitle(), show.getReleaseDate());
    }

}
