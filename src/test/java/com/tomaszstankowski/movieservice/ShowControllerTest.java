package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.controller.ShowController;
import com.tomaszstankowski.movieservice.model.Genre;
import com.tomaszstankowski.movieservice.model.Movie;
import com.tomaszstankowski.movieservice.model.Serial;
import com.tomaszstankowski.movieservice.service.ShowService;
import com.tomaszstankowski.movieservice.service.exception.InvalidShowException;
import com.tomaszstankowski.movieservice.service.exception.ShowAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.ShowNotFoundException;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ShowController.class, secure = false)
public class ShowControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @MockBean
    private ShowService service;

    @Autowired
    private WebApplicationContext context;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                mappingJackson2HttpMessageConverter);
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(context).build();
        Movie movie = new Movie("The Dark Knight Rises",
                "Batman.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 165,
                1084439099);
        Serial serial = new Serial("Breaking bad",
                "Drugs.",
                new GregorianCalendar(2008, 0, 20).getTime(),
                "USA",
                (short) 5);
        serial.getGenres().add(new Genre("drama"));
        serial.getGenres().add(new Genre("crime"));
        movie.getGenres().add(new Genre("action"));
        movie.getGenres().add(new Genre("sci-fi"));
        when(service.findMovie(1L)).thenReturn(movie);
        when(service.findSerial(2L)).thenReturn(serial);
    }

    @Test
    public void post_whenShowAdded_statusCreated() throws Exception {
        Movie body = new Movie("The Dark Knight",
                "Batman.",
                new GregorianCalendar(2008, 6, 14).getTime(),
                "USA",
                (short) 152,
                1004558444);
        body.getGenres().add(new Genre("action"));
        body.getGenres().add(new Genre("sci-fi"));
        mockMvc.perform(post("/shows/movies/add")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isCreated());

    }

    @Test
    public void post_whenTitleEmpty_statusUnprocessableEntity() throws Exception {
        Movie body = new Movie("",
                "description",
                new GregorianCalendar(1990, 11, 20).getTime(),
                "UK",
                (short) 120,
                10000000);
        doThrow(new InvalidShowException()).when(service).addMovie(body);
        mockMvc.perform(post("/shows/movies/add")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenTitleNull_statusUnprocessableEntity() throws Exception {
        Serial body = new Serial(null,
                "dd",
                new GregorianCalendar(2000, 1, 1).getTime(),
                "Poland",
                (short) 3);
        doThrow(new InvalidShowException())
                .when(service).addSerial(body);
        mockMvc.perform(post("/shows/series/add")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenShowAlreadyExists_statusConflict() throws Exception {
        Movie body = new Movie("The Dark Knight Rises",
                "For you.",
                new GregorianCalendar(2012, 6, 16).getTime(),
                "USA",
                (short) 110,
                111);
        doThrow(new ShowAlreadyExistsException(body.getTitle(), body.getReleaseDate()))
                .when(service).addMovie(body);
        mockMvc.perform(post("/shows/movies/add")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void get_whenMovieExists_statusOkJsonCorrect() throws Exception {
        mockMvc.perform(get("/shows/movies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.title", is("The Dark Knight Rises")))
                .andExpect(jsonPath("$.description", is("Batman.")))
                .andExpect(jsonPath("$.releaseDate", is("2012-07-16")))
                .andExpect(jsonPath("$.location", is("USA")))
                .andExpect(jsonPath("$.genres", containsInAnyOrder(
                        new JSONObject(new HashMap<String, Object>() {{
                            put("name", "action");
                        }}),
                        new JSONObject(new HashMap<String, Object>() {{
                            put("name", "sci-fi");
                        }})
                )))
                .andExpect(jsonPath("$.duration", is(165)))
                .andExpect(jsonPath("$.boxoffice", is(1084439099)));
    }

    @Test
    public void get_whenSerialExists_statusOkJsonCorrect() throws Exception {
        mockMvc.perform(get("/shows/series/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.title", is("Breaking bad")))
                .andExpect(jsonPath("$.description", is("Drugs.")))
                .andExpect(jsonPath("$.releaseDate", is("2008-01-20")))
                .andExpect(jsonPath("$.location", is("USA")))
                .andExpect(jsonPath("$.genres", containsInAnyOrder(
                        new JSONObject(new HashMap<String, Object>() {{
                            put("name", "drama");
                        }}),
                        new JSONObject(new HashMap<String, Object>() {{
                            put("name", "crime");
                        }})
                )))
                .andExpect(jsonPath("$.seasons", is(5)));
    }

    @Test
    public void get_whenShowNotExists_statusNotFound() throws Exception {
        when(service.findMovie(3L)).thenReturn(null);
        mockMvc.perform(get("/shows/movies/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenShowEdited_statusOk() throws Exception {
        Movie body = new Movie("The Dark Knight Rises",
                "Film o batmanie.",
                new GregorianCalendar(2012, 7, 16).getTime(),
                "USA",
                (short) 165,
                1084439555);
        body.getGenres().clear();
        body.getGenres().add(new Genre("akcja"));
        mockMvc.perform(put("/shows/movies/{id}/edit", 1L)
                .content(json(body))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenShowNotExists_statusNotFound() throws Exception {
        Movie body = new Movie("The Dark Knight Rises",
                "Film o batmanie.",
                new GregorianCalendar(2012, 7, 16).getTime(),
                "USA",
                (short) 165,
                1084439555);
        doThrow(new ShowNotFoundException(3L)).when(service).editMovie(3L, body);
        mockMvc.perform(put("/shows/movies/{id}/edit", 3L)
                .content(json(body))
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
