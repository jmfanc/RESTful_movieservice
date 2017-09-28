package com.tomaszstankowski.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomaszstankowski.movieservice.controller.ShowController;
import com.tomaszstankowski.movieservice.controller.exception.InternalExceptionHandler;
import com.tomaszstankowski.movieservice.model.Genre;
import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.model.dto.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.MovieDTO;
import com.tomaszstankowski.movieservice.model.dto.SerialDTO;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
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
import java.util.GregorianCalendar;

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
        serial.getGenres().add(new Genre("drama"));
        serial.getGenres().add(new Genre("crime"));
        movie.getGenres().add(new Genre("action"));
        movie.getGenres().add(new Genre("sci-fi"));
        movieDTO = modelMapper.fromEntity(movie);
        serialDTO = modelMapper.fromEntity(serial);
    }

    @Test
    public void post_whenShowAdded_statusCreated() throws Exception {
        when(service.addMovie(modelMapper.fromDTO(movieDTO))).thenReturn(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/movies/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenTitleEmpty_statusUnprocessableEntity() throws Exception {
        movieDTO.setTitle("");
        doThrow(new InvalidShowException()).when(service).addMovie(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/movies/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenTitleNull_statusUnprocessableEntity() throws Exception {
        serialDTO.setTitle(null);
        doThrow(new InvalidShowException())
                .when(service).addSerial(modelMapper.fromDTO(serialDTO));
        mockMvc.perform(post("/shows/series/add")
                .content(json(serialDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenShowAlreadyExists_statusConflict() throws Exception {
        doThrow(new ShowAlreadyExistsException(movieDTO.getTitle(), movieDTO.getReleaseDate()))
                .when(service).addMovie(modelMapper.fromDTO(movieDTO));
        mockMvc.perform(post("/shows/movies/add")
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void get_whenMovieExists_statusOkJsonCorrect() throws Exception {
        when(service.findMovie(1L)).thenReturn(movie);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray genres = new JSONArray();
        genres.addAll(movieDTO.getGenres());

        mockMvc.perform(get("/shows/movies/{id}", 1L))
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
        when(service.findSerial(2L)).thenReturn(serial);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray genres = new JSONArray();
        genres.addAll(serialDTO.getGenres());

        mockMvc.perform(get("/shows/series/{id}", 2L))
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
        when(service.findMovie(3L)).thenReturn(null);
        mockMvc.perform(get("/shows/movies/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenShowEdited_statusOk() throws Exception {
        movieDTO.setDescription("Bateman.");
        movieDTO.getGenres().clear();
        movieDTO.getGenres().add("thriller");
        mockMvc.perform(put("/shows/movies/{id}/edit", 1L)
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenShowNotExists_statusNotFound() throws Exception {
        doThrow(new ShowNotFoundException(3L)).when(service).editMovie(3L, modelMapper.fromDTO(movieDTO));
        mockMvc.perform(put("/shows/movies/{id}/edit", 3L)
                .content(json(movieDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenShowRemoved_statusOk() throws Exception {
        mockMvc.perform(delete("/shows/movies/{id}/delete", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenShowNotExists_statusNotFound() throws Exception {
        doThrow(new ShowNotFoundException(3L))
                .when(service).removeMovie(3L);
        mockMvc.perform(delete("/shows/movies/{id}/delete", 3L))
                .andExpect(status().isNotFound());
    }
}
