package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.Genre;
import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.Show;
import com.tomaszstankowski.movieservice.repository.GenreRepository;
import com.tomaszstankowski.movieservice.repository.MovieRepository;
import com.tomaszstankowski.movieservice.repository.SerialRepository;
import com.tomaszstankowski.movieservice.repository.ShowRepository;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShowServiceTest {

    @Mock
    private ShowRepository showRepo;
    @Mock
    private GenreRepository genreRepo;
    @Mock
    private MovieRepository movieRepo;
    @Mock
    private SerialRepository serialRepo;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private ShowService service;

    private Movie invalidBody = new Movie("", "desc", new Date(), "USA", (short) 120, 1000);

    private Genre action = new Genre("action");
    private Genre sciFi = new Genre("sci-fi");
    private Genre drama = new Genre("drama");
    private Genre crime = new Genre("crime");

    @Before
    public void setup() {
        Movie movie = new Movie(
                "The Dark Knight Rises",
                "Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 165,
                1084439099);
        Serial serial = new Serial(
                "Narcos",
                "Drugs.",
                new GregorianCalendar(2015, 1, 1).getTime(),
                "USA",
                (short) 3);

        movie.getGenres().add(action);
        movie.getGenres().add(sciFi);
        serial.getGenres().add(drama);
        when(showRepo.findOne(1L)).thenReturn(movie);
        when(showRepo.findOne(2L)).thenReturn(serial);
        when(showRepo.findByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())).thenReturn(movie);
        when(movieRepo.findOne(1L)).thenReturn(movie);
        when(serialRepo.findOne(2L)).thenReturn(serial);
        when(genreRepo.findOne("action")).thenReturn(action);
        when(genreRepo.findOne("sci-fi")).thenReturn(sciFi);
        when(genreRepo.findOne("drama")).thenReturn(drama);
        when(genreRepo.findOne("crime")).thenReturn(crime);
        service = new ShowService(showRepo, movieRepo, serialRepo, genreRepo);
    }

    @Test
    public void add_whenBodyInvalid_ThrowExc() {
        exception.expect(InvalidShowException.class);

        service.addMovie(invalidBody);

        verifyZeroInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void add_whenShowAlreadyExists_ThrowExc() {
        Movie body = new Movie(
                "The Dark Knight Rises",
                "Movie about Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA, UK",
                (short) 160,
                1084439000);
        exception.expect(ShowAlreadyExistsException.class);

        service.addMovie(body);

        verify(showRepo, times(1)).findByTitleAndReleaseDate(body.getTitle(), body.getReleaseDate());
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void add_successful() {
        Movie body = new Movie(
                "The Godfather",
                "Mafia.",
                new GregorianCalendar(1972, 2, 15).getTime(),
                "USA",
                (short) 175,
                245066411);
        body.getGenres().add(drama);
        body.getGenres().add(crime);

        service.addMovie(body);

        verify(showRepo, times(1)).findByTitleAndReleaseDate(body.getTitle(), body.getReleaseDate());
        verify(genreRepo, times(1)).findOne(drama.getName());
        verify(genreRepo, times(1)).findOne(crime.getName());
        verify(showRepo, times(1)).save(body);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    public void edit_whenBodyInvalid_ThrowExc() {
        exception.expect(InvalidShowException.class);

        service.editMovie(1L, invalidBody);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void edit_whenShowNotExits_ThrowExc() {
        Movie body = new Movie("Test", "desc", new Date(), "USA", (short) 120, 1000);
        exception.expect(ShowNotFoundException.class);

        service.editMovie(404L, body);

        verify(showRepo, times(1)).findOne(404L);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void edit_successful() {
        Movie body = new Movie(
                "The Dark Knight Rises",
                "Movie about Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA, UK",
                (short) 165,
                1084430000);
        body.getGenres().add(drama);
        Movie movie = service.findMovie(1L);

        service.editMovie(1L, body);

        assertEquals(body.getDescription(), movie.getDescription());
        assertEquals(body.getLocation(), movie.getLocation());
        assertEquals(body.getBoxoffice(), movie.getBoxoffice());
        assertTrue(movie.getGenres().contains(drama));
        assertFalse(movie.getGenres().contains(sciFi));
        assertTrue(drama.getShows().contains(movie));
        assertFalse(sciFi.getShows().contains(movie));

        verify(genreRepo, times(1)).findOne(drama.getName());
        verify(showRepo, times(1)).save(movie);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    public void remove_whenShowNotExists_ThrowExc() {
        exception.expect(ShowNotFoundException.class);

        service.removeMovie(555L);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void remove_successful() {
        Show show = showRepo.findOne(2L);
        service.removeSerial(2L);

        verify(showRepo, times(1)).findOne(2L);
        verify(showRepo, times(1)).delete(show);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

}
