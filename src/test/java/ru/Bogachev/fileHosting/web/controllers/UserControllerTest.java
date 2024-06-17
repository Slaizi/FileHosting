package ru.Bogachev.fileHosting.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.security.TokenProvider;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    private static final String DATABASE_NAME = "fake-db";
    private static final String USER_ID = "ddb54c6e-28c9-41e0-b5cb-67cbeee9ec65";

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.1-alpine"
    )
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.close();
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @SneakyThrows
    void correctGetUserByIdTest() {
        User user = userService.getById(UUID.fromString(USER_ID));

        String url = UriComponentsBuilder
                .fromPath("/api/v1/user/{id}")
                .buildAndExpand(USER_ID)
                .toUriString();


        this.mockMvc.perform(get(url)
                        .with(user(user.getUsername()).roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @SneakyThrows
    void incorrectGetUserByIdIllegalArgumentExceptionTest() {
        User user = userService.getById(UUID
                .fromString(USER_ID));

        MvcResult result = this.mockMvc.perform(get("/api/v1/user/ ")
                        .with(user(user.getUsername()).roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        JsonNode responseNode = this.objectMapper.readTree(response);
        assertEquals("User id cannot be empty.", responseNode.path("message").asText());
    }

    @Test
    @SneakyThrows
    void incorrectGetUserById() {
        User user = userService.getById(UUID.fromString(USER_ID));

        String invalidId = "invalid-uuid";

        String url = UriComponentsBuilder
                .fromPath("/api/v1/user/{id}")
                .buildAndExpand(invalidId)
                .toUriString();

        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user(user.getUsername()).roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode node = this.objectMapper.readTree(response);


        String examination = "Id with '%s' is not correct.".formatted(invalidId);
        assertEquals(examination, node.path("message").asText());
    }

    @Test
    @SneakyThrows
    void badCredentialsGetUserByIdTest() {
        String username = "test";
        User user = userService.getByUsername(username);
        String userId = user.getId().toString();

        String url = UriComponentsBuilder
                .fromPath("/api/v1/user/{id}")
                .buildAndExpand(userId)
                .toUriString();

        MvcResult result = this.mockMvc.perform(get(url)
                        .with(user("test").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("Forbidden.", response);
    }

    @Test
    @SneakyThrows
    void correctGetAllUsersTest() {
        this.mockMvc.perform(get("/api/v1/user/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("test").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[?(@.username == 'test')]", hasSize(greaterThan(0))));
    }

    @Test
    @SneakyThrows
    void badCredentialsGetAllUsersTest() {
        MvcResult result = this.mockMvc.perform(get("/api/v1/user/all")
                        .with(user("test").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("Forbidden.", response);
    }

    @Test
    @SneakyThrows
    void correctUpdateUserTest() {
        User user = userService.getByUsername("test");
        String accessToken = tokenProvider.createAccessToken(user);

        String updateUsername = "newUsername";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId().toString());
        jsonObject.put("username", updateUsername);
        jsonObject.put("password", "pass");
        jsonObject.put("passwordConformation", "pass");

        String json = jsonObject.toString();

        this.mockMvc.perform(put("/api/v1/user/update")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .with(user("test").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.id").value(user.getId().toString())
                )
                .andExpect(jsonPath("$.username").value(updateUsername));
    }

    @Test
    @SneakyThrows
    void badCredentialsUserUpdateTest() {
        User authUser = userService.getByUsername("test1");
        String accessToken = tokenProvider.createAccessToken(authUser);
        User updateUser = userService.getByUsername("test");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", updateUser.getId().toString());
        jsonObject.put("username", "updateUsername");
        jsonObject.put("password", "pass");
        jsonObject.put("passwordConformation", "pass");

        String json = jsonObject.toString();

        this.mockMvc.perform(put("/api/v1/user/update")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .with(user("test1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied."));
    }
}
