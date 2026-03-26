package com.example.demo.google;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test using MockWebServer (NO real API calls)
 */
@SpringBootTest
class GoogleBookServiceMockServerTests {

    static MockWebServer server;

    @Autowired
    private GoogleBookService googleBookService;

    @BeforeAll
    static void startServer() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        server.shutdown();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("google.books.base-url", () -> server.url("/").toString());
    }

    // ✅ Utility to enqueue JSON
    private void enqueueJson(String fileName, int status) throws IOException {
        Path path = Paths.get("src", "test", "resources", fileName);
        String body = Files.readString(path);

        server.enqueue(new MockResponse()
                .setResponseCode(status)
                .addHeader("Content-Type", "application/json")
                .setBody(body));
    }

    // ===============================
    // ✅ 1. Happy Path
    // ===============================
    @Test
    void testSearchBooks_HappyPath() throws Exception {

        enqueueJson("effectivejava.json", 200);

        GoogleBook result = googleBookService.searchBooks("effective+java", 5, 0);

        assertThat(result).isNotNull();
        assertThat(result.items()).isNotEmpty();
        assertThat(result.items().get(0).volumeInfo().title())
                .isEqualTo("Effective Java");
    }

    // ===============================
    // ✅ 2. Failure Path (No Items)
    // ===============================
    @Test
    void testSearchBooks_NoResults() throws Exception {

        String emptyResponse = """
        {
          "kind": "books#volumes",
          "totalItems": 0,
          "items": []
        }
        """;

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(emptyResponse));

        GoogleBook result = googleBookService.searchBooks("unknown", 5, 0);

        assertThat(result).isNotNull();
        assertThat(result.items()).isEmpty();
    }

    // ===============================
    // ✅ 3. Exception Scenarios (4xx & 5xx)
    // ===============================
    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500, 503})
    void testSearchBooks_ErrorResponses(int statusCode) {

        server.enqueue(new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error\":\"Something went wrong\"}"));

        assertThrows(RuntimeException.class, () -> {
            googleBookService.searchBooks("error", 5, 0);
        });
    }
}