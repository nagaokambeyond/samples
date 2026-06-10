package com.example.demo.api.controller;

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
class BooksApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @Test
    void getBookSearchReturnsOkWhenTitleIsMissing() throws Exception {
        final var response = get("/api/books/search?page=0&size=10");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getBookSearchReturnsBadRequestWhenPageIsNegative() throws Exception {
        final var response = get("/api/books/search?title=spring&page=-1&size=10");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void getBookSearchReturnsBadRequestWhenSizeIsZero() throws Exception {
        final var response = get("/api/books/search?title=spring&page=0&size=0");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private HttpResponse<String> get(String path) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
