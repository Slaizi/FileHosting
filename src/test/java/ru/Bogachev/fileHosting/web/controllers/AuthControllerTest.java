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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.dto.auth.JwtRefresh;
import ru.Bogachev.fileHosting.web.dto.auth.JwtRequest;
import ru.Bogachev.fileHosting.web.dto.user.UserDto;
import ru.Bogachev.fileHosting.web.security.TokenProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String DATABASE_NAME = "fake-db";

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
    private ObjectMapper objectMapper;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserService userService;

    @Test
    @SneakyThrows
    void correctLoginTest() {
        JwtRequest request = new JwtRequest();
        request.setUsername("test");
        request.setPassword("test");

        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpectAll(
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.accessToken").isNotEmpty(),
                        jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void badCredentialsLoginTest() {
        JwtRequest request = new JwtRequest();
        request.setUsername("test");
        request.setPassword("asd");

        MvcResult mvcResult = this.mockMvc
                .perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals("Unauthorized.", responseBody);
    }

    @Test
    @SneakyThrows
    void validatedLoginRequestTest() {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(new JwtRequest()))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        JsonNode responseNode = this.objectMapper.readTree(contentAsString);
        JsonNode errorsNode = responseNode.path("errors");


        assertTrue(contentAsString.contains("Validation failed."));
        assertTrue(errorsNode.isObject());
        assertEquals("Username cannot be null.", errorsNode.get("username").asText());
        assertEquals("Password cannot be null.", errorsNode.get("password").asText());
    }

    @Test
    @SneakyThrows
    void refreshTokensRequestTest() {
        User user = userService.getByUsername("test");
        String refreshToken = this.tokenProvider.createRefreshToken(user);
        JwtRefresh refresh = new JwtRefresh();
        refresh.setRefreshToken(refreshToken);

        this.mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(refresh)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpectAll(
                        jsonPath("$.id").isNotEmpty(),
                        jsonPath("$.accessToken").isNotEmpty(),
                        jsonPath("$.refreshToken").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void validatedRefreshTokensRequestTest() {
        MvcResult result = this.mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(new JwtRefresh())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode responseNode = this.objectMapper.readTree(response);
        JsonNode errorsNode = responseNode.path("errors");

        assertTrue(response.contains("Validation failed."));
        assertTrue(errorsNode.isObject());
        assertEquals("Refresh token cannot be empty.", errorsNode.get("refreshToken").asText());
    }

    @Test
    @SneakyThrows
    void correctRegistrationTest() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "username");
        jsonObject.put("password", "password");
        jsonObject.put("passwordConformation", "password");

        String json = jsonObject.toString();

        this.mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("username"));
    }

    @Test
    @SneakyThrows
    void badCredentialsRegistrationTestUserAlreadyExists() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "test");
        jsonObject.put("password", "213sa");
        jsonObject.put("passwordConformation", "124a");

        String json = jsonObject.toString();

        this.mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User 'test' already exists."));
    }

    @Test
    @SneakyThrows
    void badCredentialsRegistrationTestPasswordDoNotMatch() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "fdhmdfhdjnvcx");
        jsonObject.put("password", "213sa");
        jsonObject.put("passwordConformation", "124a");

        String json = jsonObject.toString();

        this.mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Password and password confirmation do not match.")
                );
    }

    @Test
    @SneakyThrows
    void validatedRegistrationRequestTest() {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsBytes(new UserDto()))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        JsonNode responseNode = this.objectMapper.readTree(contentAsString);
        JsonNode errorsNode = responseNode.path("errors");


        assertTrue(contentAsString.contains("Validation failed."));
        assertTrue(errorsNode.isObject());
        assertEquals("Username must be not null.", errorsNode.get("username").asText());
        assertEquals("Password must be not null", errorsNode.get("password").asText());
        assertEquals("Password confirmation must be not null.", errorsNode.get("passwordConformation").asText());
    }
}
