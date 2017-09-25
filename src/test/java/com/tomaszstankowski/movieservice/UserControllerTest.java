package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.controller.UserController;
import com.tomaszstankowski.movieservice.model.Sex;
import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.service.UserService;
import com.tomaszstankowski.movieservice.service.exception.InvalidUserException;
import com.tomaszstankowski.movieservice.service.exception.UserNotFoundException;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private WebApplicationContext context;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private User user;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(context).build();
        user = new User("krzysiek21", "Krzysztof Jarzyna", "kj@o2.pl", Sex.MALE);
        when(service.find(user.getLogin()))
                .thenReturn(user);
    }

    @Test
    public void get_whenUserNotExists_statusNotFound() throws Exception {
        String login = "someuser";
        when(service.find(login)).thenReturn(null);
        mockMvc.perform(get("/users/{login}", login))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_whenUserExists_statusOkJsonCorrect() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        mockMvc.perform(get("/users/{login}", user.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.mail", is(user.getMail())))
                .andExpect(jsonPath("$.joined", is(format.format(user.getJoined()))))
                .andExpect(jsonPath("$.sex", is(user.getSex().toString())));

    }

    @Test
    public void post_whenUserAdded_statusCreated() throws Exception {
        User body = new User("steve", "Steve Maden", "steve@steve.com", Sex.MALE);
        mockMvc.perform(post("/users/add")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void post_whenBodyInvalid_statusUnprocessableEntity() throws Exception {
        User emptyLoginBody = new User("", "Ewelina", "dd@o2.pl", Sex.FEMALE);
        User nullMailBody = new User("romek21", "Roman", null, Sex.MALE);
        doThrow(new InvalidUserException()).when(service).add(emptyLoginBody);
        doThrow(new InvalidUserException()).when(service).add(nullMailBody);
        mockMvc.perform(post("/users/add")
                .content(json(emptyLoginBody))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(post("/users/add")
                .content(json(nullMailBody))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void put_whenMovieEdited_statusOk() throws Exception {
        User body = new User(
                user.getLogin(),
                user.getName(),
                "kjarzyna@gmail.com",
                user.getSex()
        );
        mockMvc.perform(put("/users/{login}/edit", user.getLogin())
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void put_whenMovieNotExists_statusNotFound() throws Exception {
        User body = new User(
                user.getLogin(),
                user.getName(),
                "kjarzyna@gmail.com",
                user.getSex()
        );
        doThrow(UserNotFoundException.class).when(service).edit(user.getLogin(), body);

        mockMvc.perform(put("/users/{login}/edit", user.getLogin())
                .content(json(body))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_whenMovieRemoved_statusOk() throws Exception {
        mockMvc.perform(delete("/users/{login}/delete", user.getLogin()))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_whenMovieNotExists_statusNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(service).delete(user.getLogin());

        mockMvc.perform(delete("/users/{login}/delete", user.getLogin()))
                .andExpect(status().isNotFound());
    }
}
