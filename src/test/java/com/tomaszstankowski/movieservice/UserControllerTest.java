package com.tomaszstankowski.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomaszstankowski.movieservice.controller.UserController;
import com.tomaszstankowski.movieservice.controller.exception.InternalExceptionHandler;
import com.tomaszstankowski.movieservice.model.ModelMapper;
import com.tomaszstankowski.movieservice.model.dto.UserDTO;
import com.tomaszstankowski.movieservice.model.entity.User;
import com.tomaszstankowski.movieservice.model.enums.Sex;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.not_found.UserNotFoundException;
import com.tomaszstankowski.movieservice.service.exception.unproccessable.InvalidUserException;
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
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    private ModelMapper modelMapper = new ModelMapper();

    private ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private UserDTO userDTO;

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = standaloneSetup(new UserController(service, modelMapper))
                .setControllerAdvice(new InternalExceptionHandler())
                .build();
        user = new User("krzysiek21", "password", "Krzysztof Jarzyna", "kj@o2.pl", Sex.MALE);
        user.setDateJoined(new Date());
        userDTO = modelMapper.fromEntity(user);

    }

    @Test
    public void get_whenUserNotExists_statusNotFound() throws Exception {
        String login = "someuser";
        when(service.findUser(login)).thenReturn(null);

        mockMvc.perform(get("/users/{login}", login))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_whenUserExists_statusOkJsonCorrect() throws Exception {
        when(service.findUser(user.getLogin()))
                .thenReturn(user);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        mockMvc.perform(get("/users/{login}", user.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.dateJoined", is(format.format(user.getDateJoined()))))
                .andExpect(jsonPath("$.sex", is(user.getSex().toString())));

    }

    @Test
    public void post_whenUserAdded_statusCreated() throws Exception {
        when(service.addUser(user)).thenReturn(user);
        mockMvc.perform(post("/users")
                .content(json(userDTO))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenBodyInvalid_statusUnprocessableEntity() throws Exception {
        UserDTO emptyLoginBody = new UserDTO("", "Ewelina", "dd@o2.pl", Sex.FEMALE, "password");
        UserDTO nullMailBody = new UserDTO("romek21", "Roman", null, Sex.MALE, "password");
        doThrow(new InvalidUserException()).when(service).addUser(modelMapper.fromDTO(emptyLoginBody));
        doThrow(new InvalidUserException()).when(service).addUser(modelMapper.fromDTO(nullMailBody));

        mockMvc.perform(post("/users")
                .content(json(emptyLoginBody))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(post("/users")
                .content(json(nullMailBody))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void put_whenUserEdited_statusOk() throws Exception {
        userDTO.setEmail("kjarzyna@gmail.com");
        mockMvc.perform(put("/users/{login}", user.getLogin())
                .content(json(userDTO))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenMovieNotExists_statusNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(service).editUser(user);

        mockMvc.perform(put("/users/{login}", user.getLogin())
                .content(json(userDTO))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenMovieRemoved_statusNoContent() throws Exception {
        mockMvc.perform(delete("/users/{login}", user.getLogin()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_whenMovieNotExists_statusNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(service).removeUser(user.getLogin());

        mockMvc.perform(delete("/users/{login}", user.getLogin()))
                .andExpect(status().isNotFound());
    }
}
