package com.example.demo.api;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/purchases")
@Tag(name = "Purchase", description = "仕入API")
public interface PurchaseOperationApi {
    @PostMapping("/create")
    @Operation(summary = "仕入伝票登録")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(
            responseCode = "400",
            description = "リクエストエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {
                    @ExampleObject(
                        name = "invalidRequestBody",
                        summary = "リクエストボディのバリデーションエラー",
                        value = """
                            {
                              "errors": [
                                {
                                  "field": "purchaseInvoiceDate",
                                  "message": "null は許可されていません"
                                },
                                {
                                  "field": "details",
                                  "message": "空要素は許可されていません"
                                }
                              ],
                              "instance": "/api/purchases/create",
                              "status": 400,
                              "title": "リクエストバリデーションエラー"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "supplierReferenceNotFound",
                        summary = "仕入先の参照先なし",
                        value = """
                            {
                              "detail": "参照先データが存在しません: supplier(id=999)",
                              "instance": "/api/purchases/create",
                              "status": 400,
                              "title": "データバリデーション"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "認証エラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "unauthorized",
                    summary = "認証トークンなし",
                    value = """
                        {
                          "detail": "Unauthorized",
                          "instance": "/api/purchases/create",
                          "status": 401,
                          "title": "Unauthorized"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "更新競合",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "updateConflict",
                    summary = "在庫更新時の更新競合",
                    value = """
                        {
                          "detail": "他ユーザーによって更新されています",
                          "instance": "/api/purchases/create",
                          "status": 409,
                          "title": "更新競合"
                        }
                        """
                )
            )
        )
    })
    PurchaseInvoiceResponse createPurchaseInvoice(
        @RequestBody @Valid @NotNull PurchaseInvoiceCreateRequest request
    );
}
