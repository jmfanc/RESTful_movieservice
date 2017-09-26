package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.controller.PersonController;
import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.model.Sex;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.InvalidPersonException;
import com.tomaszstankowski.movieservice.service.exception.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
import net.minidev.json.JSONArray;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PersonController.class, secure = false)
public class PersonControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @MockBean
    private PersonService service;

    @Autowired
    private WebApplicationContext context;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Person person;

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
        person = new Person(
                "Janusz Gajos",
                new GregorianCalendar(1939, 8, 23).getTime(),
                "Dąbrowa Górnicza, Poland",
                Sex.MALE,
                new HashSet<Person.Proffesion>() {{
                    add(Person.Proffesion.ACTOR);
                }}
        );
    }

    @Test
    public void post_whenPersonAdded_statusCreated() throws Exception {
        when(service.addPerson(person)).thenReturn(person);
        mockMvc.perform(post("/people/add")
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenBodyInvalid_statusUnprocessableEntity() throws Exception {
        doThrow(new InvalidPersonException()).when(service).addPerson(person);

        mockMvc.perform(post("/people/add")
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenPersonAlreadyExists_statusConflict() throws Exception {
        doThrow(new PersonAlreadyExistsException(person)).when(service).addPerson(person);

        mockMvc.perform(post("/people/add")
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void get_whenPersonExists_statusOkJsonCorrect() throws Exception {
        when(service.findPerson(1L)).thenReturn(person);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray proffesions = new JSONArray();
        for (Person.Proffesion p : person.getProffesions())
            proffesions.add(p.toString());

        mockMvc.perform(get("/people/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is(person.getName())))
                .andExpect(jsonPath("$.birthDate", is(format.format(person.getBirthDate()))))
                .andExpect(jsonPath("$.birthPlace", is(person.getBirthPlace())))
                .andExpect(jsonPath("$.sex", is(person.getSex().toString())))
                .andExpect(jsonPath("$.proffesions", is(proffesions)));
    }

    @Test
    public void get_whenPersonNotExists_statusNotFound() throws Exception {
        when(service.findPerson(1L)).thenReturn(null);
        mockMvc.perform(get("/people/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenPersonEdited_statusOk() throws Exception {
        mockMvc.perform(put("/people/{id}/edit", 1L)
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenPersonNotExists_statusNotFound() throws Exception {
        doThrow(new PersonNotFoundException(1L)).when(service).editPerson(1L, person);

        mockMvc.perform(put("/people/{id}/edit", 1L)
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_whenBodyInvalid_statusUnproccesableEntity() throws Exception {
        doThrow(new InvalidPersonException()).when(service).editPerson(1L, person);

        mockMvc.perform(put("/people/{id}/edit", 1L)
                .content(json(person))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void delete_whenPersonRemoved_statusOk() throws Exception {
        mockMvc.perform(delete("/people/{id}/delete", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenPersonNotExists_statusNotFound() throws Exception {
        doThrow(new PersonNotFoundException(1L)).when(service).removePerson(1L);

        mockMvc.perform(delete("/people/{id}/delete", 1L))
                .andExpect(status().isNotFound());

    }
}
