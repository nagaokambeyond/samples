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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthOperationApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @Test
    void loginReturnsBearerTokenWhenCredentialIsValid() throws Exception {
        final var response = postLogin("admin", "password");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("username").asText()).isEqualTo("admin");
        assertThat(json.get("tokenType").asText()).isEqualTo("Bearer");
        assertThat(json.get("accessToken").asText()).isNotBlank();
        assertThat(json.get("expiresIn").asLong()).isEqualTo(3600L);
    }

    @Test
    void loginReturnsUnauthorizedWhenCredentialIsInvalid() throws Exception {
        final var response = postLogin("admin", "wrong-password");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(json.get("title").asText()).isEqualTo("認証エラー");
    }

    @Test
    void loginReturnsBadRequestWhenRequestBodyIsInvalid() throws Exception {
        final var response = postLoginRequest(
            """
            {
              "username": " ",
              "password": ""
            }
            """
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(json)).contains("username", "password");
    }

    @Test
    void getBookSearchReturnsOkWhenTokenIsMissing() throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/search?page=0"))
            .GET()
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void protectedApiReturnsUnauthorizedWhenTokenIsMissing() throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                """
                {
                  "title": "認証なし登録",
                  "releaseDate": "2026-01-01",
                  "publisherId": 1,
                  "genreId": 1,
                  "isbn": "9784000000601"
                }
                """
            ))
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private HttpResponse<String> postLogin(String username, String password) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password)
            ))
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postLoginRequest(String requestBody) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<String> getErrorFields(com.fasterxml.jackson.databind.JsonNode json) {
        final var fields = new ArrayList<String>();
        json.get("errors").forEach(error -> fields.add(error.get("field").asText()));
        return fields;
    }
}
