package com.tomaszstankowski.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomaszstankowski.movieservice.controller.ShowController;
import com.tomaszstankowski.movieservice.controller.exception.InternalExceptionHandler;
import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.MovieDTO;
import com.tomaszstankowski.movieservice.model.dto.ParticipationDTO;
import com.tomaszstankowski.movieservice.model.dto.SerialDTO;
import com.tomaszstankowski.movieservice.model.entity.*;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.already_exists.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidRatingException;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ParticipationNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.PersonNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.RatingNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ShowNotFoundException;
import net.minidev.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ShowControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Mock
    private ShowService service;

    private ModelMapper modelMapper = new ModelMapper();

    private ObjectMapper objectMapper = new ObjectMapper();

    private Movie movie;

    private Serial serial;

    private Person actor;

    private Participation participation;

    private ParticipationDTO participationDTO;

    private MovieDTO movieDTO;

    private SerialDTO serialDTO;

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = standaloneSetup(new ShowController(service, modelMapper))
                .setControllerAdvice(new InternalExceptionHandler())
                .build();
        movie = new Movie("The Dark Knight Rises",
                "Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 165,
                1084439099);

        serial = new Serial("Breaking bad",
                "Drugs.",
                new GregorianCalendar(2008, 0, 20).getTime(),
                "USA",
                (short) 5);

        actor = new Person(
                "Christian Bale",
                new GregorianCalendar(1974, 0, 31).getTime(),
                "Haverfordwest, Wales, UK",
                Sex.MALE
        );
        actor.getProfessions().add(Profession.ACTOR);

        participation = new Participation(Profession.ACTOR, "as Batman", actor, movie);

        serial.getGenres().add(new Genre("drama"));
        serial.getGenres().add(new Genre("crime"));
        movie.getGenres().add(new Genre("action"));
        movie.getGenres().add(new Genre("sci-fi"));

        movieDTO = modelMapper.fromEntity(movie);
        serialDTO = modelMapper.fromEntity(serial);
        participationDTO = modelMapper.fromEntity(participation);
        //request body omits person and show,
        // their ids must be forwarded as params
        // so their bodies are redundant
        participationDTO.setPerson(null);
        participationDTO.setShow(null);
    }

    @Test
    public void post_whenShowAdded_statusCreated() throws Exception {
        when(service.addShow(modelMapper.fromDTO(movieDTO))).thenReturn(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenTitleEmpty_statusUnprocessableEntity() throws Exception {
        movieDTO.setTitle("");
        doThrow(new InvalidShowException()).when(service).addShow(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenTitleNull_statusUnprocessableEntity() throws Exception {
        serialDTO.setTitle(null);
        doThrow(new InvalidShowException())
                .when(service).addShow(modelMapper.fromDTO(serialDTO));
        mockMvc.perform(post("/shows/add")
                .content(json(serialDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenShowAlreadyExists_statusConflict() throws Exception {
        doThrow(new ShowAlreadyExistsException(movieDTO.getTitle(), movieDTO.getReleaseDate()))
                .when(service).addShow(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void post_whenParticipationAdded_statusCreated() throws Exception {
        when(service.addParticipation(1L, 1L, modelMapper.fromDTO(participationDTO)))
                .thenReturn(modelMapper.fromDTO(participationDTO));
        mockMvc.perform(post("/shows/{id}/participations/add", 1L)
                .param("person", "1")
                .content(json(participationDTO))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenParticipationPersonNotExists_statusNotFound() throws Exception {
        doThrow(PersonNotFoundException.class)
                .when(service).addParticipation(1L, 1L, participation);
        mockMvc.perform(post("/shows/{id}/participations/add", 1L)
                .param("person", "1")
                .content(json(participationDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void post_whenParticipationShowNotExists_statusNotFound() throws Exception {
        doThrow(ShowNotFoundException.class)
                .when(service).addParticipation(1L, 1L, participation);
        mockMvc.perform(post("/shows/{id}/participations/add", 1L)
                .param("person", "1")
                .content(json(participationDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void post_whenRated_statusCreated() throws Exception {
        mockMvc.perform(post("/shows/{id}/rate", 1L)
                .param("login", "jandaciuk")
                .param("rating", "8"))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenRatingInvalid_statusUnproccessableEntity() throws Exception {
        doThrow(InvalidRatingException.class)
                .when(service).rate(1L, "jandaciuk", (short) 0);

        mockMvc.perform(post("/shows/{id}/rate", 1L)
                .param("login", "jandaciuk")
                .param("rating", "0"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void get_whenMovieExists_statusOkJsonCorrect() throws Exception {
        when(service.findShow(1L)).thenReturn(movie);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray genres = new JSONArray();
        genres.addAll(movieDTO.getGenres());

        mockMvc.perform(get("/shows/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.title", is(movie.getTitle())))
                .andExpect(jsonPath("$.description", is(movie.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(format.format(movie.getReleaseDate()))))
                .andExpect(jsonPath("$.location", is(movie.getLocation())))
                .andExpect(jsonPath("$.genres", is(genres)))
                .andExpect(jsonPath("$.duration", is((int) movie.getDuration())))
                .andExpect(jsonPath("$.boxoffice", is(movie.getBoxoffice())));
    }

    @Test
    public void get_whenSerialExists_statusOkJsonCorrect() throws Exception {
        when(service.findShow(2L)).thenReturn(serial);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray genres = new JSONArray();
        genres.addAll(serialDTO.getGenres());

        mockMvc.perform(get("/shows/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.title", is(serial.getTitle())))
                .andExpect(jsonPath("$.description", is(serial.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(format.format(serial.getReleaseDate()))))
                .andExpect(jsonPath("$.location", is(serial.getLocation())))
                .andExpect(jsonPath("$.genres", is(genres)))
                .andExpect(jsonPath("$.seasons", is((int) serial.getSeasons())));
    }

    @Test
    public void get_whenShowNotExists_statusNotFound() throws Exception {
        when(service.findShow(3L)).thenReturn(null);
        mockMvc.perform(get("/shows/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_whenParticipationsMovieFound_statusOkCorrectJson() throws Exception {
        List<Participation> list = new ArrayList<>();
        list.add(participation);
        when(service.findParticipations(1L, Profession.ACTOR))
                .thenReturn(list);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray professions = new JSONArray();
        actor.getProfessions().stream().map(Enum::toString).forEach(professions::add);
        JSONArray genres = new JSONArray();
        genres.addAll(movieDTO.getGenres());

        mockMvc.perform(get("/shows/{id}/participations", 1L)
                .param("role", "ACTOR"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].role", is(participation.getRole().toString())))
                .andExpect(jsonPath("$.[0].info", is(participation.getInfo())))
                .andExpect(jsonPath("$.[0].person.name", is(actor.getName())))
                .andExpect(jsonPath("$.[0].person.birthDate", is(format.format(actor.getBirthDate()))))
                .andExpect(jsonPath("$.[0].person.birthPlace", is(actor.getBirthPlace())))
                .andExpect(jsonPath("$.[0].person.sex", is(actor.getSex().toString())))
                .andExpect(jsonPath("$.[0].person.professions", is(professions)))
                .andExpect(jsonPath("$.[0].show.title", is(movie.getTitle())))
                .andExpect(jsonPath("$.[0].show.description", is(movie.getDescription())))
                .andExpect(jsonPath("$.[0].show.releaseDate", is(format.format(movie.getReleaseDate()))))
                .andExpect(jsonPath("$.[0].show.location", is(movie.getLocation())))
                .andExpect(jsonPath("$.[0].show.duration", is((int) movie.getDuration())))
                .andExpect(jsonPath("$.[0].show.boxoffice", is(movie.getBoxoffice())))
                .andExpect(jsonPath("$.[0].show.genres", is(genres)));
    }

    @Test
    public void get_whenParticipationsSerialNotExists_statusNotFound() throws Exception {
        doThrow(ShowNotFoundException.class)
                .when(service).findParticipations(1L, Profession.ACTOR);

        mockMvc.perform(get("/shows/{id}/participations", 1L)
                .param("role", "ACTOR"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_genres_statusOkJsonCorrect() throws Exception {
        List<Genre> genres = Stream
                .of(movie.getGenres(), serial.getGenres())
                .flatMap(Set::stream)
                .sorted(Comparator.comparing(Genre::getName))
                .collect(Collectors.toList());
        when(service.findAllGenres()).thenReturn(genres);

        mockMvc.perform(get("/shows/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[0]", is(genres.get(0).getName())))
                .andExpect(jsonPath("$.[1]", is(genres.get(1).getName())))
                .andExpect(jsonPath("$.[2]", is(genres.get(2).getName())))
                .andExpect(jsonPath("$.[3]", is(genres.get(3).getName())));
    }

    @Test
    public void put_whenShowEdited_statusOk() throws Exception {
        movieDTO.setDescription("Bateman.");
        movieDTO.getGenres().clear();
        movieDTO.getGenres().add("thriller");
        mockMvc.perform(put("/shows/{id}/edit", 1L)
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenShowNotExists_statusNotFound() throws Exception {
        doThrow(new ShowNotFoundException(3L)).when(service).editShow(3L, modelMapper.fromDTO(movieDTO));
        mockMvc.perform(put("/shows/{id}/edit", 3L)
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenParticipationEdited_statusOk() throws Exception {
        when(service.findShow(1L)).thenReturn(movie);
        ParticipationDTO body = new ParticipationDTO(Profession.DIRECTOR, "");

        mockMvc.perform(put("/shows/{showId}/participations/{participationId}/edit",
                1L, 1L)
                .content(json(body))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenShowOfParticipationNotExists_statusNotFound() throws Exception {
        when(service.findShow(1L)).thenReturn(null);
        ParticipationDTO body = new ParticipationDTO(Profession.DIRECTOR, "");

        mockMvc.perform(put("/shows/{showId}/participations/{participationId}/edit",
                1L, 1L)
                .content(json(body))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenParticipationNotExists_statusNotFound() throws Exception {
        when(service.findShow(1L)).thenReturn(movie);
        ParticipationDTO body = new ParticipationDTO(Profession.DIRECTOR, "");
        doThrow(ParticipationNotFoundException.class)
                .when(service).editParticipation(1L, modelMapper.fromDTO(body));

        mockMvc.perform(put("/shows/{showId}/participations/{participationId}/edit",
                1L, 1L)
                .content(json(body))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenShowRemoved_statusOk() throws Exception {
        mockMvc.perform(delete("/shows/{id}/delete", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenShowNotExists_statusNotFound() throws Exception {
        doThrow(new ShowNotFoundException(3L))
                .when(service).removeShow(3L);
        mockMvc.perform(delete("/shows/{id}/delete", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenRatingNotExists_statusNotFound() throws Exception {
        doThrow(RatingNotFoundException.class)
                .when(service).removeRating(1L, "jandaciuk");

        mockMvc.perform(delete("/shows/{id}/rate", 1L)
                .param("login", "jandaciuk"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenRatingDeleted_statusOk() throws Exception {
        mockMvc.perform(delete("/shows/{id}/rate", 1L)
                .param("login", "jandaciuk"))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenParticipationNotExists_statusNotFound() throws Exception {
        when(service.findShow(1L)).thenReturn(movie);
        doThrow(ParticipationNotFoundException.class)
                .when(service).removeParticipation(1L);

        mockMvc.perform(delete("/shows/{showId}/participations/{participationId}/delete",
                1L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenShowOfParticipationNotExists_statusNotFound() throws Exception {
        when(service.findShow(1L)).thenReturn(null);

        mockMvc.perform(delete("/shows/{showId}/participations/{participationId}/delete",
                1L, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenParticipationDeleted_statusOk() throws Exception {
        when(service.findShow(1L)).thenReturn(movie);

        mockMvc.perform(delete("/shows/{showId}/participations/{participationId}/delete",
                1L, 1L))
                .andExpect(status().isOk());
    }
}
