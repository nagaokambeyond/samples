package com.example.demo.api.controller

import com.example.demo.openbd.generated.api.BooksApi
import com.example.demo.openbd.generated.invoker.ApiException
import com.example.demo.openbd.generated.model.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.List

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class OpenBdBooksApiControllerTest {
    @LocalServerPort
    private val port = 0

    @MockitoBean
    private val openBdBooksApi: BooksApi? = null

    @BeforeEach
    fun setUp() {
        Mockito.reset<BooksApi?>(openBdBooksApi)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsOpenBdResponse() {
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn("9784780802047", null))
            .thenReturn(List.of<BookDto?>(book("9784780802047", "おにぎりレシピ101")))

        val response = get("/api/books/openbd?isbn=9784780802047")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get(0).get("summary").get("isbn").asText()).isEqualTo("9784780802047")
        Assertions.assertThat(json.get(0).get("summary").get("title").asText()).isEqualTo("おにぎりレシピ101")
        Assertions.assertThat(json.get(0).get("onix").get("RecordReference").asText()).isEqualTo("9784780802047")
        Assertions.assertThat(json.get(0).get("onix").get("ProductIdentifier").get("IDValue").asText())
            .isEqualTo("9784780802047")
        Assertions.assertThat(json.get(0).get("hanmoto").get("datemodified").asText()).isEqualTo("2025-12-26 11:32:36")
        Mockito.verify<BooksApi?>(openBdBooksApi)!!.getBooksByIsbn("9784780802047", null)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnPassesCommaSeparatedIsbns() {
        val isbn = "9784780802047,9784003101018"
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn(isbn, null))
            .thenReturn(
                List.of<BookDto?>(
                    book("9784780802047", "おにぎりレシピ101"),
                    book("9784003101018", "こころ")
                )
            )

        val response = get("/api/books/openbd?isbn=" + isbn)

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        val captor = ArgumentCaptor.forClass<String?, String?>(String::class.java)
        Mockito.verify<BooksApi?>(openBdBooksApi)!!.getBooksByIsbn(captor.capture() ?: "", Mockito.isNull<String?>())
        Assertions.assertThat(captor.getValue()).isEqualTo(isbn)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsNotFoundWhenOpenBdBookIsNotFound() {
        val isbn = "9784780802047,9784003101018"
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn(isbn, null))
            .thenReturn(
                Arrays.asList<BookDto?>(
                    null,
                    book("9784003101018", "こころ")
                )
            )

        val response = get("/api/books/openbd?isbn=" + isbn)
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("OpenBD書誌なし")
        Mockito.verify<BooksApi?>(openBdBooksApi)!!.getBooksByIsbn(isbn, null)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsNotFoundWhenOpenBdResponseIsNull() {
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn("9784780802047", null))
            .thenReturn(null)

        val response = get("/api/books/openbd?isbn=9784780802047")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("OpenBD書誌なし")
        Mockito.verify<BooksApi?>(openBdBooksApi)!!.getBooksByIsbn("9784780802047", null)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsNotFoundWhenOpenBdResponseIsEmpty() {
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn("9784780802047", null))
            .thenReturn(mutableListOf<BookDto?>())

        val response = get("/api/books/openbd?isbn=9784780802047")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("OpenBD書誌なし")
        Mockito.verify<BooksApi?>(openBdBooksApi)!!.getBooksByIsbn("9784780802047", null)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsBadRequestWhenIsbnIsMissing() {
        val response = get("/api/books/openbd")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Mockito.verifyNoInteractions(openBdBooksApi)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsBadRequestWhenIsbnIsBlank() {
        val response = get("/api/books/openbd?isbn=")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Mockito.verifyNoInteractions(openBdBooksApi)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsBadRequestWhenIsbnFormatIsInvalid() {
        val response = get("/api/books/openbd?isbn=9784780802047,invalid")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Mockito.verifyNoInteractions(openBdBooksApi)
    }

    @Test
    @Throws(Exception::class)
    fun getBooksByIsbnReturnsBadGatewayWhenOpenBdApiFails() {
        Mockito.`when`<MutableList<BookDto?>?>(openBdBooksApi!!.getBooksByIsbn("9784780802047", null))
            .thenThrow(ApiException(500, "OpenBD error"))

        val response = get("/api/books/openbd?isbn=9784780802047")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_GATEWAY.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("外部API呼び出しエラー")
        Assertions.assertThat(json.get("detail").asText()).isEqualTo("OpenBD APIの呼び出しに失敗しました")
    }

    @Throws(Exception::class)
    private fun get(path: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun book(isbn: String, title: String): BookDto? {
        return BookDto()
            .onix(
                OnixDto()
                    .recordReference(isbn)
                    .notificationType("03")
                    .productIdentifier(
                        OnixProductIdentifierDto()
                            .productIDType("15")
                            .idValue(isbn)
                    )
            )
            .hanmoto(
                HanmotoDto()
                    .datemodified("2025-12-26 11:32:36")
                    .datecreated("2014-02-25 11:29:44")
            )
            .summary(
                SummaryDto()
                    .isbn(isbn)
                    .title(title)
                    .volume("")
                    .series("")
                    .publisher("")
                    .pubdate("")
                    .author("")
            )
    }

    companion object {
        private val HTTP_CLIENT: HttpClient = HttpClient.newHttpClient()
        private val OBJECT_MAPPER = ObjectMapper()

        @AfterAll
        fun closeHttpClient() {
            HTTP_CLIENT.close()
        }
    }
}
