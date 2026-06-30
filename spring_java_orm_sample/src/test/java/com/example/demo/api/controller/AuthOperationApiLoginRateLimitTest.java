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

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "app.auth.login-rate-limit.daily-limit=2"
)
class AuthOperationApiLoginRateLimitTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @Test
    void loginReturnsTooManyRequestsWhenDailyLimitIsExceeded() throws Exception {
        assertThat(postLogin("rate-limit-user", "wrong-password").statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(postLogin("rate-limit-user", "wrong-password").statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final var response = postLogin("rate-limit-user", "wrong-password");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエスト回数制限");
        assertThat(json.get("detail").asText()).isEqualTo("ログインリクエスト回数が日次上限を超えました");
    }

    @Test
    void loginRateLimitDoesNotAffectDifferentUsername() throws Exception {
        postLogin("first-user", "wrong-password");
        postLogin("first-user", "wrong-password");

        final var response = postLogin("second-user", "wrong-password");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void loginRateLimitDoesNotAffectOtherApis() throws Exception {
        postLogin("api-search-user", "wrong-password");
        postLogin("api-search-user", "wrong-password");
        postLogin("api-search-user", "wrong-password");

        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/search?page=0"))
            .GET()
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
}
