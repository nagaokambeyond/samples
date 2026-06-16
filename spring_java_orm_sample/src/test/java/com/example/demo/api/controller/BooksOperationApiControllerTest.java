package com.example.demo.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksOperationApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @Test
    void getBooksReturnsNotFound() throws Exception {
        final var response = get("/api/books");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getBookSearchReturnsOkWhenTitleIsMissing() throws Exception {
        final var response = get("/api/books/search?page=0");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getBookSearchReturnsBadRequestWhenPageIsNegative() throws Exception {
        final var response = get("/api/books/search?keyword=spring&page=-1");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void getBookSearchReturnsConfiguredPageSize() throws Exception {
        final var response = get("/api/books/search?keyword=spring&page=0");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("size").asInt()).isEqualTo(10);
    }

    @Test
    void getBookSearchReturnsGenreId() throws Exception {
        final var response = get("/api/books/search?keyword=spring&page=0");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("content").get(0).get("genreId").asLong()).isEqualTo(5L);
        assertThat(json.get("content").get(0).get("genreName").asText()).isEqualTo("工学");
    }

    private HttpResponse<String> get(String path) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
