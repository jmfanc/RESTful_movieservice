package com.tomaszstankowski.movieservice;

import com.tomaszstankowski.movieservice.model.Sex;
import com.tomaszstankowski.movieservice.model.User;
import com.tomaszstankowski.movieservice.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private List<User> users = new ArrayList<>();

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
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
        userRepository.deleteAllInBatch();
        users.add(new User("krzysiek21", "Krzysztof Jarzyna", "kj@o2.pl", Sex.MALE));
        users.add(new User("dd", "Krzysztof Ibisz", "kkk@gmail.com", Sex.MALE));
        userRepository.save(users.get(0));
        userRepository.save(users.get(1));
    }

    @Test
    public void whenUserNotExistsStatusNotFound() throws Exception {
        mockMvc.perform(get("/users/bambo"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getMethodReturnsCorrectJson() throws Exception {
        mockMvc.perform(get("/users/krzysiek21"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.login", is("krzysiek21")))
                .andExpect(jsonPath("$.name", is("Krzysztof Jarzyna")))
                .andExpect(jsonPath("$.mail", is("kj@o2.pl")))
                .andExpect(jsonPath("$.sex", is(Sex.MALE.toString())));

    }

    @Test
    public void getMethodWithNameParameterOk() throws Exception {
        mockMvc.perform(get("/users?name=Krzysztof"))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/users?name=Jarzyna"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void userRemainsTheSameAfterPersistence() throws Exception {
        User persisted = new User("steve", "Steve Maden", "steve@steve.com", Sex.MALE);
        userRepository.save(persisted);
        User fetched = userRepository.findOne("steve");
        assertNotSame(persisted, fetched);
        assertEquals(persisted.getLogin(), fetched.getLogin());
        assertEquals(persisted.getName(), fetched.getName());
        assertEquals(persisted.getMail(), fetched.getMail());
        assertEquals(persisted.getSex(), fetched.getSex());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(format.format(persisted.getJoined()), format.format(fetched.getJoined()));
    }

    @Test
    public void whenPostBodyIsInvalidStatusIsUnprocessableEntity() throws Exception {
        User invalidUser1 = new User("", "Ewelina", "dd@o2.pl", Sex.FEMALE);
        User invalidUser2 = new User("login", "Roman", null, Sex.MALE);
        mockMvc.perform(post("/users/add")
                .content(this.json(invalidUser1))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(post("/users/add")
                .content(this.json(invalidUser2))
                .contentType(contentType))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void putMethodChangesObjectInDB() throws Exception {
        User body = new User("krzysiek21", "Krzysztof Szczecina", "ks@ks.pl", Sex.FEMALE);
        mockMvc.perform(put("/users/krzysiek21/edit")
                .content(this.json(body))
                .contentType(contentType))
                .andExpect(status().isNoContent());
        User fetched = userRepository.findOne("krzysiek21");
        assertEquals(fetched.getName(), "Krzysztof Szczecina");
    }

    @Test
    public void deleteMethodRemovesObjectFromDB() throws Exception {
        mockMvc.perform(delete("/users/krzysiek21"))
                .andExpect(status().isNoContent());
        assertNull(userRepository.findOne("krzysiek21"));
    }
}
