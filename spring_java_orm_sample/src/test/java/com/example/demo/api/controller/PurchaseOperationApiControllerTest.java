package com.example.demo.api.controller;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.service.PurchaseOperationService;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PurchaseOperationApiControllerTest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @MockitoBean
    private PurchaseOperationService purchaseOperationService;

    @AfterAll
    static void closeHttpClient() {
        HTTP_CLIENT.close();
    }

    @BeforeEach
    void setUp() {
        reset(purchaseOperationService);
    }

    @Test
    void createPurchaseInvoiceReturnsOkAndResponse() throws Exception {
        final var updateAt = LocalDateTime.of(2026, 2, 1, 10, 0);
        final var responseBody = new PurchaseInvoiceResponse();
        responseBody.setId(10L);
        responseBody.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        responseBody.setPurchaseInvoiceDate(LocalDate.of(2026, 2, 1));
        responseBody.setSupplierId(1L);
        responseBody.setReceivingStoreId(2L);
        responseBody.setPurchaseInvoiceAmount(3500L);
        responseBody.setUpdateAt(updateAt);
        responseBody.setVersion(1L);
        responseBody.setDetail(List.of(
            purchaseInvoiceDetailResponse(20L, 10L, 1L, 1000, 2, 2000L, updateAt),
            purchaseInvoiceDetailResponse(21L, 10L, 2L, 500, 3, 1500L, updateAt)
        ));
        when(purchaseOperationService.create(any())).thenReturn(responseBody);

        final var response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailBookId": 1,
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                },
                {
                  "purchaseInvoiceDetailBookId": 2,
                  "purchaseInvoiceDetailUnitPrice": 500,
                  "purchaseInvoiceDetailQuantity": 3
                }
              ]
            }
            """
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(json.get("id").asLong()).isEqualTo(10L);
        assertThat(json.get("purchaseInvoiceType").asText()).isEqualTo("PURCHASE");
        assertThat(json.get("purchaseInvoiceDate").asText()).isEqualTo("2026-02-01");
        assertThat(json.get("supplierId").asLong()).isEqualTo(1L);
        assertThat(json.get("receivingStoreId").asLong()).isEqualTo(2L);
        assertThat(json.get("purchaseInvoiceAmount").asLong()).isEqualTo(3500L);
        assertThat(json.get("version").asLong()).isEqualTo(1L);
        assertThat(json.get("detail").get(0).get("id").asLong()).isEqualTo(20L);
        assertThat(json.get("detail").get(0).get("purchaseInvoiceDetailAmount").asLong()).isEqualTo(2000L);
        assertThat(json.get("detail").get(1).get("id").asLong()).isEqualTo(21L);
        assertThat(json.get("detail").get(1).get("purchaseInvoiceDetailAmount").asLong()).isEqualTo(1500L);

        final var captor = ArgumentCaptor.forClass(PurchaseInvoiceCreateRequest.class);
        verify(purchaseOperationService).create(captor.capture());
        final var request = captor.getValue();
        assertThat(request.getPurchaseInvoiceDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(request.getSupplierId()).isEqualTo(1L);
        assertThat(request.getReceivingStoreId()).isEqualTo(2L);
        assertThat(request.getDetails()).hasSize(2);
        assertThat(request.getDetails().getFirst().getPurchaseInvoiceDetailBookId()).isEqualTo(1L);
        assertThat(request.getDetails().getFirst().getPurchaseInvoiceDetailUnitPrice()).isEqualTo(1000);
        assertThat(request.getDetails().getFirst().getPurchaseInvoiceDetailQuantity()).isEqualTo(2);
    }

    @Test
    void createPurchaseInvoiceReturnsBadRequestWithFieldErrorsWhenRequestBodyIsInvalid() throws Exception {
        final var response = post(
            """
            {
              "purchaseInvoiceDate": null,
              "supplierId": null,
              "receivingStoreId": null,
              "details": []
            }
            """
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(json)).contains("purchaseInvoiceDate", "supplierId", "receivingStoreId", "details");

        verifyNoInteractions(purchaseOperationService);
    }

    @Test
    void createPurchaseInvoiceReturnsBadRequestWhenDetailFieldsAreInvalid() throws Exception {
        final var response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 1,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailBookId": null,
                  "purchaseInvoiceDetailUnitPrice": 0,
                  "purchaseInvoiceDetailQuantity": 1001
                }
              ]
            }
            """
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(json))
            .contains(
                "details[0].purchaseInvoiceDetailBookId",
                "details[0].purchaseInvoiceDetailUnitPrice",
                "details[0].purchaseInvoiceDetailQuantity"
            );

        verifyNoInteractions(purchaseOperationService);
    }

    @Test
    void createPurchaseInvoiceReturnsBadRequestWhenForeignKeyDoesNotExist() throws Exception {
        when(purchaseOperationService.create(any()))
            .thenThrow(new ForeignKeyReferenceNotFoundException("supplier", 999L));

        final var response = post(
            """
            {
              "purchaseInvoiceDate": "2026-02-01",
              "supplierId": 999,
              "receivingStoreId": 2,
              "details": [
                {
                  "purchaseInvoiceDetailBookId": 1,
                  "purchaseInvoiceDetailUnitPrice": 1000,
                  "purchaseInvoiceDetailQuantity": 2
                }
              ]
            }
            """
        );
        final var json = OBJECT_MAPPER.readTree(response.body());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(json.get("title").asText()).isEqualTo("データバリデーション");
        assertThat(json.get("detail").asText()).isEqualTo("参照先データが存在しません: supplier(id=999)");
    }

    private HttpResponse<String> post(String requestBody) throws Exception {
        final var request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/purchases/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<String> getErrorFields(JsonNode json) {
        final var fields = new ArrayList<String>();
        json.get("errors").forEach(error -> fields.add(error.get("field").asText()));
        return fields;
    }

    private PurchaseInvoiceDetailResponse purchaseInvoiceDetailResponse(
        Long id,
        Long purchaseInvoiceId,
        Long purchaseInvoiceDetailBookId,
        Integer purchaseInvoiceDetailUnitPrice,
        Integer purchaseInvoiceDetailQuantity,
        Long purchaseInvoiceDetailAmount,
        LocalDateTime updateAt
    ) {
        final var response = new PurchaseInvoiceDetailResponse();
        response.setId(id);
        response.setPurchaseInvoiceId(purchaseInvoiceId);
        response.setPurchaseInvoiceDetailBookId(purchaseInvoiceDetailBookId);
        response.setPurchaseInvoiceDetailUnitPrice(purchaseInvoiceDetailUnitPrice);
        response.setPurchaseInvoiceDetailQuantity(purchaseInvoiceDetailQuantity);
        response.setPurchaseInvoiceDetailAmount(purchaseInvoiceDetailAmount);
        response.setUpdateAt(updateAt);
        response.setVersion(1L);
        return response;
    }
}
