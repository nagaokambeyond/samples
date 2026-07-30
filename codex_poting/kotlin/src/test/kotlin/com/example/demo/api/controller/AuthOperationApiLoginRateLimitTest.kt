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

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["app.auth.login-rate-limit.daily-limit=2"]
)
internal class AuthOperationApiLoginRateLimitTest {
    @LocalServerPort
    private val port = 0

    @Test
    @Throws(Exception::class)
    fun loginReturnsTooManyRequestsWhenDailyLimitIsExceeded() {
        Assertions.assertThat(postLogin("rate-limit-user", "wrong-password").statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value())
        Assertions.assertThat(postLogin("rate-limit-user", "wrong-password").statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value())

        val response = postLogin("rate-limit-user", "wrong-password")
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエスト回数制限")
        Assertions.assertThat(json.get("detail").asText()).isEqualTo("ログインリクエスト回数が日次上限を超えました")
    }

    @Test
    @Throws(Exception::class)
    fun loginRateLimitDoesNotAffectDifferentUsername() {
        postLogin("first-user", "wrong-password")
        postLogin("first-user", "wrong-password")

        val response = postLogin("second-user", "wrong-password")

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    @Throws(Exception::class)
    fun loginRateLimitDoesNotAffectOtherApis() {
        postLogin("api-search-user", "wrong-password")
        postLogin("api-search-user", "wrong-password")
        postLogin("api-search-user", "wrong-password")

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/books/search?page=0"))
            .GET()
            .build()
        val response: HttpResponse<String?> = HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    @Throws(Exception::class)
    fun resetLoginRateLimitClearsExceededCounters() {
        postLogin("reset-user", "wrong-password")
        postLogin("reset-user", "wrong-password")
        Assertions.assertThat(postLogin("reset-user", "wrong-password").statusCode())
            .isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value())

        val token = login()
        val resetResponse = postResetLoginRateLimit(token)
        val response = postLogin("reset-user", "wrong-password")

        Assertions.assertThat(resetResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    @Throws(Exception::class)
    fun resetLoginRateLimitReturnsUnauthorizedWhenTokenIsMissing() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login-rate-limit/reset"))
            .POST(HttpRequest.BodyPublishers.noBody())
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
    private fun login(): String? {
        val response = postLogin("admin", "password")
        return OBJECT_MAPPER.readTree(response.body()).get("accessToken").asText()
    }

    @Throws(Exception::class)
    private fun postResetLoginRateLimit(token: String?): HttpResponse<String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/auth/login-rate-limit/reset"))
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()
        return HTTP_CLIENT.send<String?>(request, HttpResponse.BodyHandlers.ofString())
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
