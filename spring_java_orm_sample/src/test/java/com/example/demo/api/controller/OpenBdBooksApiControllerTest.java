package com.example.demo.api.controller;

import com.example.demo.openbd.generated.api.BooksApi;
import com.example.demo.openbd.generated.invoker.ApiException;
import com.example.demo.openbd.generated.model.BookDto;
import com.example.demo.openbd.generated.model.SummaryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenBdBooksApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @MockitoBean
    private BooksApi openBdBooksApi;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @BeforeEach
    void setUp() {
        reset(openBdBooksApi);
    }

    @Test
    void getBooksByIsbnReturnsOpenBdResponse() throws Exception {
        when(openBdBooksApi.getBooksByIsbn("9784780802047", null))
            .thenReturn(List.of(book("9784780802047", "おにぎりレシピ101")));

        final var response = get("/api/books/openbd?isbn=9784780802047");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get(0).get("summary").get("isbn").asText()).isEqualTo("9784780802047");
        assertThat(json.get(0).get("summary").get("title").asText()).isEqualTo("おにぎりレシピ101");
        verify(openBdBooksApi).getBooksByIsbn("9784780802047", null);
    }

    @Test
    void getBooksByIsbnPassesCommaSeparatedIsbns() throws Exception {
        final var isbn = "9784780802047,9784003101018";
        when(openBdBooksApi.getBooksByIsbn(isbn, null))
            .thenReturn(List.of(
                book("9784780802047", "おにぎりレシピ101"),
                book("9784003101018", "こころ")
            ));

        final var response = get("/api/books/openbd?isbn=" + isbn);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final var captor = ArgumentCaptor.forClass(String.class);
        verify(openBdBooksApi).getBooksByIsbn(captor.capture(), org.mockito.Mockito.isNull());
        assertThat(captor.getValue()).isEqualTo(isbn);
    }

    @Test
    void getBooksByIsbnReturnsBadRequestWhenIsbnIsMissing() throws Exception {
        final var response = get("/api/books/openbd");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifyNoInteractions(openBdBooksApi);
    }

    @Test
    void getBooksByIsbnReturnsBadRequestWhenIsbnIsBlank() throws Exception {
        final var response = get("/api/books/openbd?isbn=");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifyNoInteractions(openBdBooksApi);
    }

    @Test
    void getBooksByIsbnReturnsBadRequestWhenIsbnFormatIsInvalid() throws Exception {
        final var response = get("/api/books/openbd?isbn=9784780802047,invalid");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifyNoInteractions(openBdBooksApi);
    }

    @Test
    void getBooksByIsbnReturnsBadGatewayWhenOpenBdApiFails() throws Exception {
        when(openBdBooksApi.getBooksByIsbn("9784780802047", null))
            .thenThrow(new ApiException(500, "OpenBD error"));

        final var response = get("/api/books/openbd?isbn=9784780802047");
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
        assertThat(json.get("title").asText()).isEqualTo("外部API呼び出しエラー");
        assertThat(json.get("detail").asText()).isEqualTo("OpenBD APIの呼び出しに失敗しました");
    }

    private HttpResponse<String> get(String path) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private BookDto book(String isbn, String title) {
        return new BookDto()
            .summary(new SummaryDto()
                .isbn(isbn)
                .title(title)
                .volume("")
                .series("")
                .publisher("")
                .pubdate("")
                .author(""));
    }
}
