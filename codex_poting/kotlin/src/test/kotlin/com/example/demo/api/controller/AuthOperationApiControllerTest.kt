package com.example.demo.api.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.function.Consumer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AuthOperationApiControllerTest {
    @LocalServerPort
    private val port = 0

    @Test
    @Throws(Exception::class)
    fun loginReturnsBearerTokenWhenCredentialIsValid() {
        val response = postLogin("admin", "password")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("username").asText()).isEqualTo("admin")
        Assertions.assertThat(json.get("tokenType").asText()).isEqualTo("Bearer")
        Assertions.assertThat(json.get("accessToken").asText()).isNotBlank()
        Assertions.assertThat(json.get("expiresIn").asLong()).isEqualTo(3600L)
    }

    @Test
    @Throws(Exception::class)
    fun loginReturnsUnauthorizedWhenCredentialIsInvalid() {
        val response = postLogin("admin", "wrong-password")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("認証エラー")
    }

    @Test
    @Throws(Exception::class)
    fun loginReturnsBadRequestWhenRequestBodyIsInvalid() {
        val response = postLoginRequest(
            """
            {
              "username": " ",
              "password": ""
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json)).contains("username", "password")
    }

    @Test
    @Throws(Exception::class)
    fun getBookSearchReturnsOkWhenTokenIsMissing() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/search?page=0"))
            .GET()
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    @Throws(Exception::class)
    fun protectedApiReturnsUnauthorizedWhenTokenIsMissing() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/create"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                  "title": "認証なし登録",
                  "releaseDate": "2026-01-01",
                  "publisherId": 1,
                  "genreId": 1,
                  "isbn": "9784000000601"
                }
                
                """.trimIndent()
                )
            )
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Throws(Exception::class)
    private fun postLogin(username: String?, password: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                {
                  "username": "%s",
                  "password": "%s"
                }
                
                """.trimIndent().format(username, password)
                )
            )
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun postLoginRequest(requestBody: String): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun getErrorFields(json: JsonNode): MutableList<String?> {
        val fields = ArrayList<String?>()
        json.get("errors").forEach(Consumer { error: JsonNode? -> fields.add(error!!.get("field").asText()) })
        return fields
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
