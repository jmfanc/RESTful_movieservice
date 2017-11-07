package com.tomaszstankowski.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomaszstankowski.movieservice.controller.PersonController;
import com.tomaszstankowski.movieservice.controller.exception.InternalExceptionHandler;
import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.PersonDTO;
import com.tomaszstankowski.movieservice.model.entity.Genre;
import com.tomaszstankowski.movieservice.model.entity.Movie;
import com.tomaszstankowski.movieservice.model.entity.Participation;
import com.tomaszstankowski.movieservice.model.entity.Person;
import com.tomaszstankowski.movieservice.model.enums.Profession;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.conflict.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.not_found.PersonNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.not_found.ShowNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidPersonException;
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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class PersonControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Mock
    private PersonService service;

    private ModelMapper modelMapper = new ModelMapper();

    private ObjectMapper objectMapper = new ObjectMapper();

    private Person person;

    private PersonDTO personDTO;

    private Participation participation;

    private Movie movie;

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = standaloneSetup(new PersonController(service, modelMapper))
                .setControllerAdvice(new InternalExceptionHandler())
                .build();
        person = new Person(
                "Janusz Gajos",
                new GregorianCalendar(1939, 8, 23).getTime(),
                "Dąbrowa Górnicza, Poland",
                Sex.MALE
        );
        person.getProfessions().add(Profession.ACTOR);
        movie = new Movie("The Dark Knight Rises",
                "Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 165,
                1084439099);
        movie.getGenres().add(new Genre("sci-fi"));
        movie.getGenres().add(new Genre("action"));
        participation = new Participation(Profession.ACTOR, "as Batman", person, movie);
        personDTO = modelMapper.fromEntity(person);
    }

    @Test
    public void post_whenPersonAdded_statusCreated() throws Exception {
        when(service.addPerson(person)).thenReturn(person);
        mockMvc.perform(post("/people")
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenBodyInvalid_statusUnprocessableEntity() throws Exception {
        doThrow(new InvalidPersonException()).when(service).addPerson(person);

        mockMvc.perform(post("/people")
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenPersonAlreadyExists_statusConflict() throws Exception {
        doThrow(new PersonAlreadyExistsException(person)).when(service).addPerson(person);

        mockMvc.perform(post("/people")
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void get_whenPersonExists_statusOkJsonCorrect() throws Exception {
        when(service.findPerson(1L)).thenReturn(person);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray proffesions = new JSONArray();
        for (Profession p : person.getProfessions())
            proffesions.add(p.toString());

        mockMvc.perform(get("/people/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(person.getName())))
                .andExpect(jsonPath("$.birthDate", is(format.format(person.getBirthDate()))))
                .andExpect(jsonPath("$.birthPlace", is(person.getBirthPlace())))
                .andExpect(jsonPath("$.sex", is(person.getSex().toString())))
                .andExpect(jsonPath("$.professions", is(proffesions)));
    }

    @Test
    public void get_whenPersonNotExists_statusNotFound() throws Exception {
        when(service.findPerson(1L)).thenReturn(null);
        mockMvc.perform(get("/people/{id}", 1L))
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
        person.getProfessions().stream().map(Enum::toString).forEach(professions::add);
        JSONArray genres = new JSONArray();
        participation.getShow().getGenres().stream().map(Genre::getName).forEach(genres::add);

        mockMvc.perform(get("/people/{id}/participations", 1L)
                .param("role", "ACTOR"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].role", is(participation.getRole().toString())))
                .andExpect(jsonPath("$.[0].info", is(participation.getInfo())))
                .andExpect(jsonPath("$.[0].person.name", is(person.getName())))
                .andExpect(jsonPath("$.[0].person.birthDate", is(format.format(person.getBirthDate()))))
                .andExpect(jsonPath("$.[0].person.birthPlace", is(person.getBirthPlace())))
                .andExpect(jsonPath("$.[0].person.sex", is(person.getSex().toString())))
                .andExpect(jsonPath("$.[0].person.professions", is(professions)))
                .andExpect(jsonPath("$.[0].show.title", is(movie.getTitle())))
                .andExpect(jsonPath("$.[0].show.description", is(movie.getDescription())))
                .andExpect(jsonPath("$.[0].show.dateReleased", is(format.format(movie.getDateReleased()))))
                .andExpect(jsonPath("$.[0].show.location", is(movie.getLocation())))
                .andExpect(jsonPath("$.[0].show.duration", is((int) movie.getDuration())))
                .andExpect(jsonPath("$.[0].show.boxoffice", is(movie.getBoxoffice())))
                .andExpect(jsonPath("$.[0].show.genres", is(genres)));
    }

    @Test
    public void get_whenParticipationsSerialNotExists_statusNotFound() throws Exception {
        doThrow(ShowNotFoundException.class)
                .when(service).findParticipations(1L, Profession.ACTOR);

        mockMvc.perform(get("/people/{id}/participations", 1L)
                .param("role", "ACTOR"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenPersonEdited_statusOk() throws Exception {
        mockMvc.perform(put("/people/{id}", 1L)
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenPersonNotExists_statusNotFound() throws Exception {
        doThrow(new PersonNotFoundException(1L)).when(service).editPerson(1L, person);

        mockMvc.perform(put("/people/{id}/edit", 1L)
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenBodyInvalid_statusUnproccesableEntity() throws Exception {
        doThrow(new InvalidPersonException()).when(service).editPerson(1L, person);

        mockMvc.perform(put("/people/{id}", 1L)
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void delete_whenPersonRemoved_statusOk() throws Exception {
        mockMvc.perform(delete("/people/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenPersonNotExists_statusNotFound() throws Exception {
        doThrow(new PersonNotFoundException(1L)).when(service).removePerson(1L);

        mockMvc.perform(delete("/people/{id}", 1L))
                .andExpect(status().isNotFound());

    }
}
