package com.example.demo.api.controller

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceDetailResponse
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.service.PurchaseOperationService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.lang.String
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.List
import kotlin.Exception
import kotlin.Int
import kotlin.Long
import kotlin.Throws
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.contains
import kotlin.ranges.contains
import kotlin.sequences.contains
import kotlin.text.contains
import kotlin.text.trimIndent

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PurchaseOperationApiControllerTest {
    @LocalServerPort
    private val port = 0

    @MockitoBean
    private val purchaseOperationService: PurchaseOperationService? = null

    @BeforeEach
    fun setUp() {
        Mockito.reset<PurchaseOperationService?>(purchaseOperationService)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsOkAndResponse() {
        val updateAt = LocalDateTime.of(2026, 2, 1, 10, 0)
        val responseBody = PurchaseInvoiceResponse()
        responseBody.id = 10L
        responseBody.purchaseInvoiceType = PurchaseInvoiceType.PURCHASE
        responseBody.purchaseInvoiceDate = LocalDate.of(2026, 2, 1)
        responseBody.supplierId = 1L
        responseBody.receivingStoreId = 2L
        responseBody.purchaseInvoiceAmount = 3500L
        responseBody.updateAt = updateAt
        responseBody.version = 1L
        responseBody.detail = List.of<PurchaseInvoiceDetailResponse>(
            purchaseInvoiceDetailResponse(20L, 10L, 1L, 1000, 2, 2000L, updateAt),
            purchaseInvoiceDetailResponse(21L, 10L, 2L, 500, 3, 1500L, updateAt)
        )
        Mockito.`when`<PurchaseInvoiceResponse?>(purchaseOperationService!!.create(anyPurchaseInvoiceCreateRequest()))
            .thenReturn(responseBody)

        val response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailIsbn": "0000000000001",
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                },
                {
                  "purchaseInvoiceDetailIsbn": "0000000000002",
                  "purchaseInvoiceDetailUnitPrice": 500,
                  "purchaseInvoiceDetailQuantity": 3
                }
              ]
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(json.get("id").asLong()).isEqualTo(10L)
        Assertions.assertThat(json.get("purchaseInvoiceType").asText()).isEqualTo("PURCHASE")
        Assertions.assertThat(json.get("purchaseInvoiceDate").asText()).isEqualTo("2026-02-01")
        Assertions.assertThat(json.get("supplierId").asLong()).isEqualTo(1L)
        Assertions.assertThat(json.get("receivingStoreId").asLong()).isEqualTo(2L)
        Assertions.assertThat(json.get("purchaseInvoiceAmount").asLong()).isEqualTo(3500L)
        Assertions.assertThat(json.get("version").asLong()).isEqualTo(1L)
        Assertions.assertThat(json.get("detail").get(0).get("id").asLong()).isEqualTo(20L)
        Assertions.assertThat(json.get("detail").get(0).get("purchaseInvoiceDetailAmount").asLong()).isEqualTo(2000L)
        Assertions.assertThat(json.get("detail").get(1).get("id").asLong()).isEqualTo(21L)
        Assertions.assertThat(json.get("detail").get(1).get("purchaseInvoiceDetailAmount").asLong()).isEqualTo(1500L)

        val captor = ArgumentCaptor.forClass<PurchaseInvoiceCreateRequest, PurchaseInvoiceCreateRequest?>(
            PurchaseInvoiceCreateRequest::class.java
        )
        Mockito.verify<PurchaseOperationService?>(purchaseOperationService)!!.create(
            captor.capture() ?: PurchaseInvoiceCreateRequest(LocalDate.of(2026, 1, 1), 1L, 1L, emptyList())
        )
        val request = captor.getValue()
        Assertions.assertThat(request.purchaseInvoiceDate).isEqualTo(LocalDate.of(2026, 2, 1))
        Assertions.assertThat(request.supplierId).isEqualTo(1L)
        Assertions.assertThat(request.receivingStoreId).isEqualTo(2L)
        Assertions.assertThat(request.details).hasSize(2)
        Assertions.assertThat(request.details!!.first().purchaseInvoiceDetailIsbn).isEqualTo("0000000000001")
        Assertions.assertThat(request.details!!.first().purchaseInvoiceDetailUnitPrice).isEqualTo(1000)
        Assertions.assertThat(request.details!!.first().purchaseInvoiceDetailQuantity).isEqualTo(2)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsBadRequestWithFieldErrorsWhenRequestBodyIsInvalid() {
        val response = post(
            """
            {
              "purchaseInvoiceDate": null,
              "supplierId": null,
              "receivingStoreId": null,
              "details": []
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json))
            .contains("purchaseInvoiceDate", "supplierId", "receivingStoreId", "details")

        Mockito.verifyNoInteractions(purchaseOperationService)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsBadRequestWhenDetailFieldsAreInvalid() {
        val response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailIsbn": null,
                  "purchaseInvoiceDetailUnitPrice": 0,
                  "purchaseInvoiceDetailQuantity": 1001
                }
              ]
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json))
            .contains(
                "details[0].purchaseInvoiceDetailIsbn",
                "details[0].purchaseInvoiceDetailUnitPrice",
                "details[0].purchaseInvoiceDetailQuantity"
            )

        Mockito.verifyNoInteractions(purchaseOperationService)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsBadRequestWhenDetailIsbnFormatIsInvalid() {
        val response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailIsbn": "000000000001",
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                },
                {
                  "purchaseInvoiceDetailIsbn": "00000000000A1",
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                }
              ]
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json))
            .contains(
                "details[0].purchaseInvoiceDetailIsbn",
                "details[1].purchaseInvoiceDetailIsbn"
            )

        Mockito.verifyNoInteractions(purchaseOperationService)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsBadRequestWhenForeignKeyDoesNotExist() {
        Mockito.`when`<PurchaseInvoiceResponse?>(purchaseOperationService!!.create(anyPurchaseInvoiceCreateRequest()))
            .thenThrow(ForeignKeyReferenceNotFoundException("supplier", 999L))

        val response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 999,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailIsbn": "0000000000001",
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                }
              ]
            }
            
            """.trimIndent()
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("データバリデーション")
        Assertions.assertThat(json.get("detail").asText()).isEqualTo("参照先データが存在しません: supplier(id=999)")
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsUnauthorizedWhenTokenIsMissing() {
        val response = postWithoutAuthorization(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailIsbn": "0000000000001",
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                }
              ]
            }
            
            """.trimIndent()
        )

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        Mockito.verifyNoInteractions(purchaseOperationService)
    }

    @Test
    @Throws(Exception::class)
    fun createPurchaseInvoiceReturnsBadRequestWhenDetailsSizeExceedsMax() {
        val detail = """
            {
              "purchaseInvoiceDetailIsbn": "0000000000001",
              "purchaseInvoiceDetailUnitPrice": 1000,
              "purchaseInvoiceDetailQuantity": 2
            }
            
            """.trimIndent()
        val response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                %s
              ]
            }
            
            """.trimIndent().format(String.join(",", Collections.nCopies<kotlin.String?>(11, detail)))
        )
        val json: JsonNode = OBJECT_MAPPER.readTree(response.body())

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        Assertions.assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー")
        Assertions.assertThat(getErrorFields(json)).contains("details")
        Mockito.verifyNoInteractions(purchaseOperationService)
    }

    @Throws(Exception::class)
    private fun post(requestBody: kotlin.String): HttpResponse<kotlin.String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/purchases/create"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + login())
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HTTP_CLIENT.send<kotlin.String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun postWithoutAuthorization(requestBody: kotlin.String): HttpResponse<kotlin.String?> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/purchases/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        return HTTP_CLIENT.send<kotlin.String?>(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(Exception::class)
    private fun login(): kotlin.String? {
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
        val response: HttpResponse<kotlin.String?> =
            HTTP_CLIENT.send<kotlin.String?>(request, HttpResponse.BodyHandlers.ofString())
        return OBJECT_MAPPER.readTree(response.body()).get("accessToken").asText()
    }

    private fun getErrorFields(json: JsonNode): MutableList<kotlin.String?> {
        val fields = ArrayList<kotlin.String?>()
        json.get("errors").forEach { error -> fields.add(error.get("field").asText()) }
        return fields
    }

    private fun anyPurchaseInvoiceCreateRequest(): PurchaseInvoiceCreateRequest {
        return ArgumentMatchers.any(PurchaseInvoiceCreateRequest::class.java)
            ?: PurchaseInvoiceCreateRequest(LocalDate.of(2026, 1, 1), 1L, 1L, emptyList())
    }

    private fun purchaseInvoiceDetailResponse(
        id: Long?,
        purchaseInvoiceId: Long?,
        purchaseInvoiceDetailBookId: Long?,
        purchaseInvoiceDetailUnitPrice: Int?,
        purchaseInvoiceDetailQuantity: Int?,
        purchaseInvoiceDetailAmount: Long?,
        updateAt: LocalDateTime?
    ): PurchaseInvoiceDetailResponse {
        val response = PurchaseInvoiceDetailResponse()
        response.id = id
        response.purchaseInvoiceId = purchaseInvoiceId
        response.purchaseInvoiceDetailBookId = purchaseInvoiceDetailBookId
        response.purchaseInvoiceDetailUnitPrice = purchaseInvoiceDetailUnitPrice
        response.purchaseInvoiceDetailQuantity = purchaseInvoiceDetailQuantity
        response.purchaseInvoiceDetailAmount = purchaseInvoiceDetailAmount
        response.updateAt = updateAt
        response.version = 1L
        return response
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
