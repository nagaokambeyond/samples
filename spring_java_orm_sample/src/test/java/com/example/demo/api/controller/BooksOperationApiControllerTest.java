package com.example.demo.api.controller;

import com.example.demo.BookRowLock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksOperationApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static String accessToken;

    @LocalServerPort
    private int port;

    @Autowired
    private DataSource dataSource;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @Test
    void getBooksReturnsUnauthorizedWhenTokenIsMissing() throws Exception {
        final var response = get("/api/books");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void getBookReturnsOkWhenTokenIsMissing() throws Exception {
        final var response = get("/api/books/1");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("id").asLong()).isEqualTo(1L);
        assertThat(json.get("isbn").asText()).isEqualTo("0000000000001");
    }

    @Test
    void getBookSearchReturnsOkWhenTitleIsMissing() throws Exception {
        final var response = get("/api/books/search?page=0");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getBookSearchReturnsBadRequestWhenPageIsNegative() throws Exception {
        final var response = get("/api/books/search?keyword=spring&page=-1");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getErrorFields(json)).contains("page");
    }

    @Test
    void getBookSearchReturnsBadRequestWhenOnlyReleaseDateFromIsSet() throws Exception {
        final var response = get("/api/books/search?releaseDateFrom=2020-01-01&page=0");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("相関バリデーション");
    }

    @Test
    void getBookSearchReturnsBadRequestWhenReleaseDateFromIsAfterReleaseDateTo() throws Exception {
        final var response = get("/api/books/search?releaseDateFrom=2020-01-02&releaseDateTo=2020-01-01&page=0");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("相関バリデーション");
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
        assertThat(json.get("content").get(0).get("isbn").asText()).isEqualTo("0000000000001");
    }

    @Test
    void createBookReturnsBadRequestWithFieldErrorsWhenRequestBodyIsInvalid() throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + login())
            .POST(HttpRequest.BodyPublishers.ofString(
                """
                {
                  "title": "",
                  "releaseDate": null,
                  "publisherId": null,
                  "genreId": null,
                  "isbn": null,
                  "salesUnitPrice": null
                }
                """
            ))
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(json)).contains("title", "releaseDate", "publisherId", "genreId", "isbn", "salesUnitPrice");
    }

    @Test
    void createBookReturnsBadRequestWhenIsbnIsInvalid() throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + login())
            .POST(HttpRequest.BodyPublishers.ofString(
                """
                {
                  "title": "ISBN不正",
                  "releaseDate": "2026-01-01",
                  "publisherId": 1,
                  "genreId": 5,
                  "isbn": "invalid",
                  "salesUnitPrice": 1200
                }
                """
            ))
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getErrorFields(json)).contains("isbn");
    }

    @Test
    void createBookReturnsIsbnWhenRequestIsValid() throws Exception {
        final var token = login();
        final var response = createBook(token, "ISBN登録", "9784000000501");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("isbn").asText()).isEqualTo("9784000000501");
        assertThat(json.get("salesUnitPrice").asInt()).isEqualTo(1200);

        delete("/api/books/" + json.get("id").asLong(), token);
    }

    @Test
    void updateBookReturnsOkAndResponse() throws Exception {
        final var token = login();
        final var isbn = randomIsbn();
        final var createBookResponse = createBook(token, "本更新HTTP", isbn);
        final var createdBook = OBJECT_MAPPER.readTree(createBookResponse.body());
        final var bookId = createdBook.get("id").asLong();

        try {
            final var response = post(
                "/api/books/update",
                """
                {
                  "id": %d,
                  "title": "本更新HTTP更新後",
                  "author": "Saburo",
                  "releaseDate": "2026-02-01",
                  "publisherId": 2,
                  "genreId": 5,
                  "isbn": "%s",
                  "version": %d
                }
                """.formatted(bookId, isbn, createdBook.get("version").asLong()),
                token
            );
            final var json = OBJECT_MAPPER.readTree(response.body());

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(json.get("id").asLong()).isEqualTo(bookId);
            assertThat(json.get("title").asText()).isEqualTo("本更新HTTP更新後");
            assertThat(json.get("author").asText()).isEqualTo("Saburo");
            assertThat(json.get("publisherId").asLong()).isEqualTo(2L);
            assertThat(json.get("isbn").asText()).isEqualTo(isbn);
        } finally {
            delete("/api/books/" + bookId, token);
        }
    }

    @Test
    void deleteBookReturnsOkAndRemovesBook() throws Exception {
        final var token = login();
        final var createBookResponse = createBook(token, "本削除HTTP", randomIsbn());
        final var createdBook = OBJECT_MAPPER.readTree(createBookResponse.body());
        final var bookId = createdBook.get("id").asLong();

        final var deleteResponse = delete("/api/books/" + bookId, token);
        final var getResponse = get("/api/books/" + bookId);
        final var json = OBJECT_MAPPER.readTree(getResponse.body());

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(deleteResponse.body()).isEmpty();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(json.get("title").asText()).isEqualTo("該当データなし");
    }

    @Test
    void createSalesUnitPriceReturnsOkWithEmptyBodyWhenRequestIsValid() throws Exception {
        final var token = login();
        final var createBookResponse = createBook(token, "本販売単価登録", randomIsbn());
        final var createdBook = OBJECT_MAPPER.readTree(createBookResponse.body());
        final var bookId = createdBook.get("id").asLong();

        try {
            final var createSalesUnitPriceResponse = postSalesUnitPrice(bookId, 1500, LocalDate.now().plusDays(30), token);

            assertThat(createSalesUnitPriceResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(createSalesUnitPriceResponse.body()).isEmpty();
        } finally {
            delete("/api/books/" + bookId, token);
        }
    }

    @Test
    void createSalesUnitPriceReturnsBadRequestWhenRequestBodyIsInvalid() throws Exception {
        final var response = post(
            "/api/books/1/sales-unit-prices",
            """
            {
              "salesUnitPrice": 0,
              "effectiveFrom": "%s"
            }
            """.formatted(LocalDate.now()),
            login()
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(json)).contains("salesUnitPrice", "effectiveFrom");
    }

    @Test
    void createSalesUnitPriceReturnsUnauthorizedWhenTokenIsMissing() throws Exception {
        final var response = postWithoutAuthorization(
            "/api/books/1/sales-unit-prices",
            """
            {
              "salesUnitPrice": 1500,
              "effectiveFrom": "%s"
            }
            """.formatted(LocalDate.now().plusDays(30))
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void createSalesUnitPriceReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        final var response = postSalesUnitPrice(999L, 1500, LocalDate.now().plusDays(30), login());
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(json.get("title").asText()).isEqualTo("該当データなし");
    }

    @Test
    void createSalesUnitPriceReturnsConflictWhenBookIsLocked() throws Exception {
        final var token = login();

        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            final var response = postSalesUnitPrice(1L, 1500, LocalDate.now().plusDays(30), token);
            final var json = OBJECT_MAPPER.readTree(response.body());

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(json.get("title").asText()).isEqualTo("更新競合");
        }
    }

    private HttpResponse<String> get(String path) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String requestBody, String token) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postWithoutAuthorization(String path, String requestBody) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path, String token) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> createBook(String token, String title, String isbn) throws Exception {
        return post(
            "/api/books/create",
            """
            {
              "title": "%s",
              "author": "Jiro",
              "releaseDate": "2026-01-01",
              "publisherId": 1,
              "genreId": 5,
              "isbn": "%s",
              "salesUnitPrice": 1200
            }
            """.formatted(title, isbn),
            token
        );
    }

    private HttpResponse<String> postSalesUnitPrice(Long bookId, Integer salesUnitPrice, LocalDate effectiveFrom, String token) throws Exception {
        return post(
            "/api/books/" + bookId + "/sales-unit-prices",
            """
            {
              "salesUnitPrice": %d,
              "effectiveFrom": "%s"
            }
            """.formatted(salesUnitPrice, effectiveFrom),
            token
        );
    }

    private String login() throws Exception {
        if (accessToken != null) {
            return accessToken;
        }

        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                """
                {
                  "username": "admin",
                  "password": "password"
                }
                """
            ))
            .build();
        final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        accessToken = OBJECT_MAPPER.readTree(response.body()).get("accessToken").asText();
        return accessToken;
    }

    private String randomIsbn() {
        return "978" + String.format("%010d", Math.floorMod(System.nanoTime(), 10_000_000_000L));
    }

    private List<String> getErrorFields(JsonNode json) {
        final var fields = new ArrayList<String>();
        json.get("errors").forEach(error -> fields.add(error.get("field").asText()));
        return fields;
    }
}
