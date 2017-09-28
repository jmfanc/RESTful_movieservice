package com.tomaszstankowski.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomaszstankowski.movieservice.controller.PersonController;
import com.tomaszstankowski.movieservice.controller.exception.InternalExceptionHandler;
import com.tomaszstankowski.movieservice.model.Person;
import com.tomaszstankowski.movieservice.model.Sex;
import com.tomaszstankowski.movieservice.model.dto.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.PersonDTO;
import com.tomaszstankowski.movieservice.service.PersonService;
import com.tomaszstankowski.movieservice.service.exception.InvalidPersonException;
import com.tomaszstankowski.movieservice.service.exception.PersonAlreadyExistsException;
import com.tomaszstankowski.movieservice.service.exception.PersonNotFoundException;
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
        person.getProfessions().add(Person.Profession.ACTOR);
        personDTO = modelMapper.fromEntity(person);
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
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void post_whenPersonAlreadyExists_statusConflict() throws Exception {
        doThrow(new PersonAlreadyExistsException(person)).when(service).addPerson(person);

        mockMvc.perform(post("/people/add")
                .content(json(personDTO))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void get_whenPersonExists_statusOkJsonCorrect() throws Exception {
        when(service.findPerson(1L)).thenReturn(person);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        JSONArray proffesions = new JSONArray();
        for (Person.Profession p : person.getProfessions())
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
    public void put_whenPersonEdited_statusOk() throws Exception {
        mockMvc.perform(put("/people/{id}/edit", 1L)
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

        mockMvc.perform(put("/people/{id}/edit", 1L)
                .content(json(personDTO))
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
