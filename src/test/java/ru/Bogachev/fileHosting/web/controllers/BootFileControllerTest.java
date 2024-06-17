package ru.Bogachev.fileHosting.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.BootFileService;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.security.TokenProvider;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
public class BootFileControllerTest {

    private static final String DATABASE_NAME = "fake-db";
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.1-alpine"
    )
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

    @Container
    public static MinIOContainer minioContainer = new MinIOContainer(
            DockerImageName.parse("minio/minio:latest")
    )
            .withCommand("server /data --console-address :9090")
            .withExposedPorts(9090, 9000)
            .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000).forStatusCode(200));


    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", () -> minioContainer.getS3URL());
        registry.add("minio.username", () -> minioContainer.getUserName());
        registry.add("minio.password", () -> minioContainer.getPassword());
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        minioContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.close();
        minioContainer.close();
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private BootFileService bootFileService;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile createTestFile() {
        return new MockMultipartFile(
                "file",
                "fileTest.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is a test file".getBytes()
        );
    }

    @Test
    @SneakyThrows
    void correctUploadFileTest() {
        User user = userService.getByUsername("test");
        String accessToken = tokenProvider.createAccessToken(user);


        this.mockMvc.perform(multipart("/api/v1/file/upload")
                        .file(createTestFile())
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .with(user(user.getUsername()).roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").isNotEmpty())
                .andExpect(jsonPath("$.originalName").value("fileTest"))
                .andExpect(jsonPath("$.fileType").value("txt"));
    }

    @Test
    @SneakyThrows
    void correctDownloadFile() {
        String userId = "3549e500-6c65-4982-acd0-4420392aa3a7";
        User user = userService.getById(UUID.fromString(userId));
        String accessToken = tokenProvider.createAccessToken(user);

        bootFileService.upload(UUID.fromString(userId), createTestFile());
        String serverName = bootFileService.getAllUserFiles(UUID.fromString(userId))
                .stream()
                .findFirst()
                .get().getServerName();

        String url = UriComponentsBuilder.fromPath("/api/v1/file/download/{serverName}")
                .buildAndExpand(serverName)
                .toUriString();


        MvcResult result = this.mockMvc.perform(get(url)
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .with(user(user.getUsername()).roles("USER"))
                        .content(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        assertEquals("This is a test file", result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    void getAllUserFiles() {
        String userId = "3549e500-6c65-4982-acd0-4420392aa3a7";
        User user = userService.getById(UUID.fromString(userId));
        String accessToken = tokenProvider.createAccessToken(user);

        bootFileService.upload(UUID.fromString(userId), createTestFile());


        MvcResult result = this.mockMvc.perform(get("/api/v1/file/all")
                        .header("Authorization", "Bearer %s".formatted(accessToken))
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .with(user(user.getUsername()).roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode node = this.objectMapper.readTree(response).get(0);

        assertNotNull(node.path("link").asText());
        assertEquals("fileTest", node.path("originalName").asText());
        assertEquals("txt", node.path("fileType").asText());
    }
}
