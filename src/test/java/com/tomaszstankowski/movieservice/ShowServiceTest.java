package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.repository.*;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.conflict.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ParticipationNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.PersonNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.RatingNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ShowNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidRatingException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidShowException;
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

    private User user;

    private Rating rating;

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

        user = new User(
                "jandaciuk",
                "password",
                "Jan Daciuk",
                "jdaciuk@gmail.com",
                Sex.MALE
        );

        participation = new Participation(Profession.ACTOR, "as Batman", actor, movie);
        actor.getParticipations().add(participation);
        movie.getParticipations().add(participation);

        rating = new Rating((short) 8, serial, user);

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
        when(showRepo.findByTitleAndDateReleased(movie.getTitle(), movie.getDateReleased())).thenReturn(movie);
        exception.expect(ShowAlreadyExistsException.class);

        service.addShow(movie);

        verify(showRepo, times(1)).findByTitleAndDateReleased(movie.getTitle(), movie.getDateReleased());
        verifyNoMoreInteractions(showRepo);
        verifyZeroInteractions(genreRepo);
    }

    @Test
    public void add_successful() {
        when(showRepo.findByTitleAndDateReleased(movie.getTitle(), movie.getDateReleased())).thenReturn(null);
        service.addShow(movie);

        verify(showRepo, times(1)).findByTitleAndDateReleased(movie.getTitle(), movie.getDateReleased());
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
    public void add_whenUserRatesShowFirstTime_createNewRating() {
        when(showRepo.findOne(1L)).thenReturn(serial);
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        when(ratingRepo.findByUserAndShow(user, serial)).thenReturn(null);

        service.addRating(1L, user.getLogin(), (short) 8);

        verify(showRepo, times(1)).findOne(1L);
        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(ratingRepo, times(1)).findByUserAndShow(user, serial);
        verify(ratingRepo, times(1)).save(rating);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(ratingRepo);
    }

    @Test
    public void add_whenUserRatesShowAgainWithDifferentRating_deletePreviousRatingAndCreateNew() {
        when(showRepo.findOne(1L)).thenReturn(serial);
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        when(ratingRepo.findByUserAndShow(user, serial)).thenReturn(rating);

        service.addRating(1L, user.getLogin(), (short) 7);

        verify(showRepo, times(1)).findOne(1L);
        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(ratingRepo, times(1)).findByUserAndShow(user, serial);
        verify(ratingRepo, times(1)).delete(rating);
        verify(ratingRepo, times(1)).save(rating);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(ratingRepo);
    }

    @Test
    public void add_whenUserRatesShowAgainWithSameRating_doNothing() {
        when(showRepo.findOne(1L)).thenReturn(serial);
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        when(ratingRepo.findByUserAndShow(user, serial)).thenReturn(rating);

        service.addRating(1L, user.getLogin(), (short) 8);

        verify(showRepo, times(1)).findOne(1L);
        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(ratingRepo, times(1)).findByUserAndShow(user, serial);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(ratingRepo);
    }

    @Test
    public void add_whenRatingInvalid_throwExc() {
        exception.expect(InvalidRatingException.class);

        service.addRating(1L, user.getLogin(), (short) 11);
        verifyZeroInteractions(showRepo);
        verifyZeroInteractions(userRepo);
        verifyZeroInteractions(ratingRepo);
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
    public void edit_showEditedSuccessful() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        Movie body = new Movie(
                movie.getTitle(),
                "Movie about batman.",
                movie.getDateReleased(),
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

        verify(showRepo, times(1)).findOne(1L);
        verify(showRepo, times(1)).save(movie);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    public void edit_participationEditedSuccessful() {
        when(participationRepo.findOne(1L)).thenReturn(participation);
        when(showRepo.findOne(1L)).thenReturn(movie);
        Participation body = new Participation(Profession.DIRECTOR,
                "",
                participation.getPerson(),
                participation.getShow());

        service.editParticipation(1L, 1L, body);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verify(participationRepo, times(1)).findOne(1L);
        verify(participationRepo, times(1)).save(participation);
        verifyNoMoreInteractions(participationRepo);
        assertEquals(body.getInfo(), participation.getInfo());
        assertEquals(body.getRole(), participation.getRole());
    }

    @Test
    public void edit_whenParticipationNotExists_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        when(participationRepo.findOne(1L)).thenReturn(null);
        Participation body = new Participation(Profession.DIRECTOR,
                "",
                participation.getPerson(),
                participation.getShow());
        exception.expect(ParticipationNotFoundException.class);

        service.editParticipation(1L, 1L, body);
        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verify(participationRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(participationRepo);
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
    public void remove_showRemovedSuccessful() {
        when(showRepo.findOne(2L)).thenReturn(serial);

        service.removeShow(2L);

        verify(showRepo, times(1)).findOne(2L);
        verify(showRepo, times(1)).delete(serial);
        verifyNoMoreInteractions(showRepo);
    }

    @Test
    public void remove_whenRatingNotExists_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(serial);
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        when(ratingRepo.findByUserAndShow(user, serial)).thenReturn(null);
        exception.expect(RatingNotFoundException.class);

        service.removeRating(1L, user.getLogin());

        verify(showRepo, times(1)).findOne(1L);
        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(ratingRepo, times(1)).findByUserAndShow(user, serial);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(ratingRepo);
    }

    @Test
    public void remove_ratingRemovedSuccessful() {
        when(showRepo.findOne(1L)).thenReturn(serial);
        when(userRepo.findOne(user.getLogin())).thenReturn(user);
        when(ratingRepo.findByUserAndShow(user, serial)).thenReturn(rating);

        service.removeRating(1L, user.getLogin());

        verify(showRepo, times(1)).findOne(1L);
        verify(userRepo, times(1)).findOne(user.getLogin());
        verify(ratingRepo, times(1)).findByUserAndShow(user, serial);
        verify(ratingRepo, times(1)).delete(rating);
        verifyNoMoreInteractions(showRepo);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(ratingRepo);
    }

    @Test
    public void remove_whenParticipationNotExists_throwExc() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        when(participationRepo.findOne(1L)).thenReturn(null);
        exception.expect(ParticipationNotFoundException.class);

        service.removeParticipation(1L, 1L);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verify(participationRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(participationRepo);
    }

    @Test
    public void remove_participationRemovedSuccessful() {
        when(showRepo.findOne(1L)).thenReturn(movie);
        when(participationRepo.findOne(1L)).thenReturn(participation);

        service.removeParticipation(1L, 1L);

        verify(showRepo, times(1)).findOne(1L);
        verifyNoMoreInteractions(showRepo);
        verify(participationRepo, times(1)).findOne(1L);
        verify(participationRepo, times(1)).delete(1L);
        verifyNoMoreInteractions(participationRepo);
    }
}
