package com.example.demo.api.controller

import com.example.demo.BookRowLock
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.util.function.Consumer
import javax.sql.DataSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BooksOperationApiControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    private val dataSource: DataSource? = null

    @Test
    @Throws(Exception::class)
    fun getBooksReturnsUnauthorizedWhenTokenIsMissing() {
        val response = get("/api/books")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    @Throws(Exception::class)
    fun getBookReturnsOkWhenTokenIsMissing() {
        val response = get("/api/books/1")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("id").asLong()).isEqualTo(1L)
        Assertions.assertThat(json.get("isbn").asText()).isEqualTo("0000000000001")
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsOkWhenTitleIsMissing() {
        val response = get("/api/books/search?page=0")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsBadRequestWhenPageIsNegative() {
        val response = get("/api/books/search?keyword=spring&page=-1")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(getErrorFields(json)).contains("page")
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsBadRequestWhenOnlyReleaseDateFromIsSet() {
        val response = get("/api/books/search?releaseDateFrom=2020-01-01&page=0")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("相関バリデーション")
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsBadRequestWhenReleaseDateFromIsAfterReleaseDateTo() {
        val response = get("/api/books/search?releaseDateFrom=2020-01-02&releaseDateTo=2020-01-01&page=0")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("相関バリデーション")
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsConfiguredPageSize() {
        val response = get("/api/books/search?keyword=spring&page=0")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("size").asInt()).isEqualTo(10)
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsGenreId() {
        val response = get("/api/books/search?keyword=spring&page=0")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("content").get(0).get("genreId").asLong()).isEqualTo(5L)
        Assertions.assertThat(json.get("content").get(0).get("genreName").asText()).isEqualTo("工学")
        Assertions.assertThat(json.get("content").get(0).get("isbn").asText()).isEqualTo("0000000000001")
    }

    @Test
    @Throws(Exception::class)
    fun createBookReturnsBadRequestWithFieldErrorsWhenRequestBodyIsInvalid() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + login())
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                  "title": "",
                  "releaseDate": null,
                  "publisherId": null,
                  "genreId": null,
                  "isbn": null,
                  "salesUnitPrice": null
                }
                
                """.trimIndent()
                )
            )
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json))
            .contains("title", "releaseDate", "publisherId", "genreId", "isbn", "salesUnitPrice")
    }

    @Test
    @Throws(Exception::class)
    fun createBookReturnsBadRequestWhenIsbnIsInvalid() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + login())
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                  "title": "ISBN不正",
                  "releaseDate": "2026-01-01",
                  "publisherId": 1,
                  "genreId": 5,
                  "isbn": "invalid",
                  "salesUnitPrice": 1200
                }
                
                """.trimIndent()
                )
            )
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(getErrorFields(json)).contains("isbn")
    }

    @Test
    @Throws(Exception::class)
    fun createBookReturnsIsbnWhenRequestIsValid() {
        val token = login()
        val response = createBook(token, "ISBN登録", "9784000000501")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("isbn").asText()).isEqualTo("9784000000501")
        Assertions.assertThat(json.get("salesUnitPrice").asInt()).isEqualTo(1200)

        delete("/api/books/" + json.get("id").asLong(), token)
    }

    @Test
    @Throws(Exception::class)
    fun updateBookReturnsOkAndResponse() {
        val token = login()
        val isbn = randomIsbn()
        val createBookResponse = createBook(token, "本更新HTTP", isbn)
        val createdBook: JsonNode = OBJECT_MAPPER.readTree(createBookResponse.body())
        val bookId = createdBook.get("id").asLong()

        try {
            val response = post(
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
                
                """.trimIndent().format(bookId, isbn, createdBook.get("version").asLong()),
                token
            )
            val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

            Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
            Assertions.assertThat(json.get("id").asLong()).isEqualTo(bookId)
            Assertions.assertThat(json.get("title").asText()).isEqualTo("本更新HTTP更新後")
            Assertions.assertThat(json.get("author").asText()).isEqualTo("Saburo")
            Assertions.assertThat(json.get("publisherId").asLong()).isEqualTo(2L)
            Assertions.assertThat(json.get("isbn").asText()).isEqualTo(isbn)
        } finally {
            delete("/api/books/" + bookId, token)
        }
    }

    @Test
    @Throws(Exception::class)
    fun deleteBookReturnsOkAndRemovesBook() {
        val token = login()
        val createBookResponse = createBook(token, "本削除HTTP", randomIsbn())
        val createdBook: JsonNode = OBJECT_MAPPER.readTree(createBookResponse.body())
        val bookId = createdBook.get("id").asLong()

        val deleteResponse = delete("/api/books/" + bookId, token)
        val getResponse = get("/api/books/" + bookId)
        val json: JsonNode = OBJECT_MAPPER.readTree(getResponse.body())

        Assertions.assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(deleteResponse.body()).isEmpty()
        Assertions.assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("該当データなし")
    }

    @Test
    @Throws(Exception::class)
    fun createSalesUnitPriceReturnsOkWithEmptyBodyWhenRequestIsValid() {
        val token = login()
        val createBookResponse = createBook(token, "本販売単価登録", randomIsbn())
        val createdBook: JsonNode = OBJECT_MAPPER.readTree(createBookResponse.body())
        val bookId = createdBook.get("id").asLong()

        try {
            val createSalesUnitPriceResponse = postSalesUnitPrice(bookId, 1500, LocalDate.now().plusDays(30), token)

            Assertions.assertThat(createSalesUnitPriceResponse.statusCode()).isEqualTo(HttpStatus.OK.value())
            Assertions.assertThat(createSalesUnitPriceResponse.body()).isEmpty()
        } finally {
            delete("/api/books/" + bookId, token)
        }
    }

    @Test
    @Throws(Exception::class)
    fun createSalesUnitPriceReturnsBadRequestWhenRequestBodyIsInvalid() {
        val response = post(
            "/api/books/1/sales-unit-prices",
            """
            {
              "salesUnitPrice": 0,
              "effectiveFrom": "%s"
            }
            
            """.trimIndent().format(LocalDate.now()),
            login()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json)).contains("salesUnitPrice", "effectiveFrom")
    }

    @Test
    @Throws(Exception::class)
    fun createSalesUnitPriceReturnsUnauthorizedWhenTokenIsMissing() {
        val response = postWithoutAuthorization(
            "/api/books/1/sales-unit-prices",
            """
            {
              "salesUnitPrice": 1500,
              "effectiveFrom": "%s"
            }
            
            """.trimIndent().format(LocalDate.now().plusDays(30))
        )

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    @Throws(Exception::class)
    fun createSalesUnitPriceReturnsNotFoundWhenBookDoesNotExist() {
        val response = postSalesUnitPrice(999L, 1500, LocalDate.now().plusDays(30), login())
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("該当データなし")
    }

    @Test
    @Throws(Exception::class)
    fun createSalesUnitPriceReturnsConflictWhenBookIsLocked() {
        val token = login()

        BookRowLock.acquire(dataSource!!, 1L).use { ignored ->
            val response = postSalesUnitPrice(1L, 1500, LocalDate.now().plusDays(30), token)
            val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

            Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value())
            Assertions.assertThat(json.get("title").asText()).isEqualTo("更新競合")
        }
    }

    @Throws(Exception::class)
    private fun get(path: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .GET()
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun post(path: String?, requestBody: String, token: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun postWithoutAuthorization(path: String?, requestBody: String): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun delete(path: String?, token: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + path))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun createBook(token: String?, title: String?, isbn: String?): HttpResponse<String?> {
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
            
            """.trimIndent().format(title, isbn),
            token
        )
    }

    @Throws(Exception::class)
    private fun postSalesUnitPrice(
        bookId: Long?,
        salesUnitPrice: Int?,
        effectiveFrom: LocalDate?,
        token: String?
    ): HttpResponse<String?> {
        return post(
            "/api/books/" + bookId + "/sales-unit-prices",
            """
            {
              "salesUnitPrice": %d,
              "effectiveFrom": "%s"
            }
            
            """.trimIndent().format(salesUnitPrice, effectiveFrom),
            token
        )
    }

    @Throws(Exception::class)
    private fun login(): String? {
        if (accessToken != null) {
            return accessToken
        }

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                  "username": "admin",
                  "password": "password"
                }
                
                """.trimIndent()
                )
            )
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        accessToken = OBJECT_MAPPER.readTree(response.body()).get("accessToken").asText()
        return accessToken
    }

    private fun randomIsbn(): String {
        return "978" + String.format("%010d", Math.floorMod(System.nanoTime(), 10000000000L))
    }

    private fun getErrorFields(json: JsonNode): MutableList<String?> {
        val fields = ArrayList<String?>()
        json.get("errors").forEach(Consumer { error: JsonNode? -> fields.add(error!!.get("field").asText()) })
        return fields
    }

    companion object {
        private val HTTP_CLIENT: HttpClient = HttpClient.newHttpClient()
        private val OBJECT_MAPPER = ObjectMapper()
        private var accessToken: String? = null

        @AfterAll
        fun closeHttpClient() {
            HTTP_CLIENT.close()
        }
    }
}
