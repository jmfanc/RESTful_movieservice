package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.repository.*;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
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
    @Mock
    private PersonRepository personRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private ParticipationRepository participationRepo;
    @Mock
    private RatingRepository ratingRepo;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private ShowService service;

    private Movie movie;

    private Serial serial;

    private Person actor;

    private Participation participation;

    private Movie invalidBody = new Movie("", "desc", new Date(), "USA", (short) 120, 1000);

    private Genre action = new Genre("action");
    private Genre sciFi = new Genre("sci-fi");
    private Genre drama = new Genre("drama");
    private Genre crime = new Genre("crime");

    @Before
    public void setup() {
        movie = new Movie(
                "The Dark Knight Rises",
                "Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 165,
                1084439099);
        serial = new Serial(
                "Narcos",
                "Drugs.",
                new GregorianCalendar(2015, 1, 1).getTime(),
                "USA",
                (short) 3);

        actor = new Person(
                "Christian Bale",
                new GregorianCalendar(1974, 0, 31).getTime(),
                "Haverfordwest, Wales, UK",
                Sex.MALE
        );

        participation = new Participation(Profession.ACTOR, "as Batman", null, null);

        movie.getGenres().add(action);
        movie.getGenres().add(sciFi);
        serial.getGenres().add(drama);
        when(genreRepo.findOne("action")).thenReturn(action);
        when(genreRepo.findOne("sci-fi")).thenReturn(sciFi);
        when(genreRepo.findOne("drama")).thenReturn(drama);
        when(genreRepo.findOne("crime")).thenReturn(crime);

        service = new ShowService(
                showRepo,
                movieRepo,
                serialRepo,
                genreRepo,
                personRepo,
                userRepo,
                participationRepo,
                ratingRepo);
    }

    @Test
    public void add_whenBodyInvalid_ThrowExc() {
        exception.expect(InvalidShowException.class);

        service.addShow(invalidBody);

        verifyZeroInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void add_whenShowAlreadyExists_ThrowExc() {
        when(showRepo.findByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())).thenReturn(movie);
        exception.expect(ShowAlreadyExistsException.class);

        service.addShow(movie);

        verify(showRepo, times(1)).findByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate());
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void add_successful() {
        when(showRepo.findByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())).thenReturn(null);
        service.addShow(movie);

        verify(showRepo, times(1)).findByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate());
        verify(genreRepo, times(1)).findOne(action.getName());
        verify(genreRepo, times(1)).findOne(sciFi.getName());
        verify(showRepo, times(1)).save(movie);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    public void add_participation_successful() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        when(personRepo.findOne(1L)).thenReturn(actor);
        service.addParticipation(1L, 1L, participation);

        verify(personRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(personRepo);
        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verify(participationRepo, times(1)).save(participation);
        verifyNoMoreInteractions(participationRepo);
    }

    @Test
    public void add_participation_whenPersonNotExists_throwExc() {
        when(personRepo.findOne(1L)).thenReturn(null);
        when(showRepo.findOne(1L)).thenReturn(movie);
        exception.expect(PersonNotFoundException.class);

        service.addParticipation(1L, 1L, participation);

        verify(personRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(personRepo);
        verifyZeroInteractions(showRepo);
    }

    @Test
    public void add_participation_whenShowNotExists_throwExc() {
        when(personRepo.findOne(1L)).thenReturn(actor);
        when(showRepo.findOne(1L)).thenReturn(null);
        exception.expect(ShowNotFoundException.class);

        service.addParticipation(1L, 1L, participation);

        verify(personRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(personRepo);
        verify(showRepo, times(1)).findOne(1L);
        verifyZeroInteractions(showRepo);
    }

    @Test
    public void edit_whenBodyInvalid_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        exception.expect(InvalidShowException.class);

        service.editShow(1L, invalidBody);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void edit_whenShowNotExits_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(null);
        exception.expect(ShowNotFoundException.class);

        service.editShow(1L, movie);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void edit_successful() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        Movie body = new Movie(
                movie.getTitle(),
                "Movie about batman.",
                movie.getReleaseDate(),
                movie.getLocation(),
                movie.getDuration(),
                111
        );
        body.getGenres().clear();
        body.getGenres().add(drama);

        service.editShow(1L, body);

        assertEquals(body.getDescription(), movie.getDescription());
        assertEquals(body.getBoxoffice(), movie.getBoxoffice());
        assertTrue(movie.getGenres().contains(drama));
        assertFalse(movie.getGenres().contains(sciFi));
        assertTrue(drama.getShows().contains(movie));
        assertFalse(sciFi.getShows().contains(movie));

        verify(genreRepo, times(1)).findOne(drama.getName());
        verify(showRepo, times(1)).findOne(1L);
        verify(showRepo, times(1)).save(movie);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    public void remove_whenShowNotExists_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(null);
        exception.expect(ShowNotFoundException.class);

        service.removeShow(1L);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
    }

    @Test
    public void remove_successful() {
        when(showRepo.findOne(2L)).thenReturn(serial);

        service.removeShow(2L);

        verify(showRepo, times(1)).findOne(2L);
        verify(showRepo, times(1)).delete(serial);
        verifyNoMoreInteractions(showRepo);
    }

}
