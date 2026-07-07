package com.example.demo.api;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/api/books")
@Tag(name = "Books", description = "本API")
public interface BooksOperationApi {
    @GetMapping("/{id}")
    @Operation(summary = "本取得")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(
            responseCode = "400",
            description = "リクエストエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "invalidBookId",
                    summary = "本IDの形式不正",
                    value = """
                        {
                          "detail": "Failed to convert 'id' with value: 'abc'",
                          "instance": "/api/books/abc",
                          "status": 400,
                          "title": "Bad Request"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "データなし",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "bookNotFound",
                    summary = "取得対象の本が存在しない",
                    value = """
                        {
                          "instance": "/api/books/999",
                          "status": 404,
                          "title": "該当データなし"
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<BookResponse> getBook(
        @Parameter(description = "本ID")
        @PathVariable Long id
    );

    @GetMapping("/search")
    @Operation(summary = "本検索")
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
                        name = "invalidPage",
                        summary = "ページ番号が負の値",
                        value = """
                            {
                              "detail": "getBookSearch.page: 0 以上の値にしてください",
                              "errors": [
                                {
                                  "field": "page",
                                  "message": "0 以上の値にしてください"
                                }
                              ],
                              "instance": "/api/books/search",
                              "status": 400,
                              "title": "リクエストエラー"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "missingReleaseDatePair",
                        summary = "発売日付の片方のみ指定",
                        value = """
                            {
                              "detail": "発売日付From、発売日付To両方設定してください。",
                              "instance": "/api/books/search",
                              "status": 400,
                              "title": "相関バリデーション"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "invalidReleaseDateRange",
                        summary = "発売日付Fromが発売日付Toより後",
                        value = """
                            {
                              "detail": "発売日付From＜＝発売日付Toにしてください。",
                              "instance": "/api/books/search",
                              "status": 400,
                              "title": "相関バリデーション"
                            }
                            """
                    )
                }
            )
        ),
    })
    BookPageResponse getBookSearch(
        @Parameter(description = "タイトルまたは著者の検索キーワード")
        @RequestParam(required = false) String keyword,
        @Parameter(description = "発売日付From")
        @RequestParam(required = false) LocalDate releaseDateFrom,
        @Parameter(description = "発売日付To")
        @RequestParam(required = false) LocalDate releaseDateTo,
        @Parameter(description = "ページ番号（0始まり）")
        @RequestParam @NotNull @Min(0) Integer page
    );

    @PostMapping("/create")
    @Operation(summary = "本登録")
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
                                  "field": "title",
                                  "message": "1 から 100 の間のサイズにしてください"
                                },
                                {
                                  "field": "releaseDate",
                                  "message": "null は許可されていません"
                                },
                                {
                                  "field": "isbn",
                                  "message": "13桁の数字にしてください"
                                }
                              ],
                              "instance": "/api/books/create",
                              "status": 400,
                              "title": "リクエストバリデーションエラー"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "publisherReferenceNotFound",
                        summary = "出版社の参照先なし",
                        value = """
                            {
                              "detail": "参照先データが存在しません: publisher(id=999)",
                              "instance": "/api/books/create",
                              "status": 400,
                              "title": "データバリデーション"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "isbnUniqueConstraintViolation",
                        summary = "ISBNの一意制約違反",
                        value = """
                            {
                              "detail": "一意制約に違反しています: book(isbn=0000000000001)",
                              "instance": "/api/books/create",
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
                          "instance": "/api/books/create",
                          "status": 401,
                          "title": "Unauthorized"
                        }
                        """
                )
            )
        )
    })
    BookResponse createBook(
        @RequestBody @Valid @NotNull BookCreateRequest request
    );

    @PostMapping("/update")
    @Operation(summary = "本更新")
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
                                  "field": "title",
                                  "message": "1 から 100 の間のサイズにしてください"
                                },
                                {
                                  "field": "releaseDate",
                                  "message": "null は許可されていません"
                                },
                                {
                                  "field": "isbn",
                                  "message": "13桁の数字にしてください"
                                }
                              ],
                              "instance": "/api/books/update",
                              "status": 400,
                              "title": "リクエストバリデーションエラー"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "publisherReferenceNotFound",
                        summary = "出版社の参照先なし",
                        value = """
                            {
                              "detail": "参照先データが存在しません: publisher(id=999)",
                              "instance": "/api/books/update",
                              "status": 400,
                              "title": "データバリデーション"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "isbnUniqueConstraintViolation",
                        summary = "ISBNの一意制約違反",
                        value = """
                            {
                              "detail": "一意制約に違反しています: book(isbn=0000000000001)",
                              "instance": "/api/books/update",
                              "status": 400,
                              "title": "データバリデーション"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "データなし",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "bookNotFound",
                    summary = "更新対象の本が存在しない",
                    value = """
                        {
                          "instance": "/api/books/update",
                          "status": 404,
                          "title": "該当データなし"
                        }
                        """
                )
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
                          "instance": "/api/books/update",
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
                    summary = "他ユーザーによる更新競合",
                    value = """
                        {
                          "detail": "他ユーザーによって更新されています",
                          "instance": "/api/books/update",
                          "status": 409,
                          "title": "更新競合"
                        }
                        """
                )
            )
        )
    })
    BookResponse updateBook(
        @RequestBody @Valid @NotNull BookUpdateRequest request
    );

    @PostMapping("/{id}/sales-unit-prices")
    @Operation(summary = "本販売単価登録")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功",
            content = @Content
        ),
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
                                  "field": "salesUnitPrice",
                                  "message": "1 以上の値にしてください"
                                },
                                {
                                  "field": "effectiveFrom",
                                  "message": "未来の日付にしてください"
                                }
                              ],
                              "instance": "/api/books/1/sales-unit-prices",
                              "status": 400,
                              "title": "リクエストバリデーションエラー"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "salesUnitPriceEffectiveFromUniqueConstraintViolation",
                        summary = "同一書籍・同一有効開始日の一意制約違反",
                        value = """
                            {
                              "detail": "一意制約に違反しています: book_sales_unit_price_history(book_id,effective_from=1,2026-08-01)",
                              "instance": "/api/books/1/sales-unit-prices",
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
                          "instance": "/api/books/1/sales-unit-prices",
                          "status": 401,
                          "title": "Unauthorized"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "データなし",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "bookNotFound",
                    summary = "本販売単価登録対象の本が存在しない",
                    value = """
                        {
                          "instance": "/api/books/999/sales-unit-prices",
                          "status": 404,
                          "title": "該当データなし"
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
                    summary = "他ユーザーによる更新競合",
                    value = """
                        {
                          "detail": "他ユーザーによって更新されています",
                          "instance": "/api/books/1/sales-unit-prices",
                          "status": 409,
                          "title": "更新競合"
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<Void> createSalesUnitPrice(
        @Parameter(description = "本ID")
        @PathVariable Long id,
        @RequestBody @Valid @NotNull BookSalesUnitPriceCreateRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(summary = "本削除")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(
            responseCode = "400",
            description = "リクエストエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "invalidBookId",
                    summary = "本IDの形式不正",
                    value = """
                        {
                          "detail": "Failed to convert 'id' with value: 'abc'",
                          "instance": "/api/books/abc",
                          "status": 400,
                          "title": "Bad Request"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "データなし",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "bookNotFound",
                    summary = "削除対象の本が存在しない",
                    value = """
                        {
                          "instance": "/api/books/999",
                          "status": 404,
                          "title": "該当データなし"
                        }
                        """
                )
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
                          "instance": "/api/books/1",
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
                    summary = "削除対象が他ユーザーにより更新中",
                    value = """
                        {
                          "detail": "他ユーザーによって更新されています",
                          "instance": "/api/books/1",
                          "status": 409,
                          "title": "更新競合"
                        }
                        """
                )
            )
        )
    })
    void deleteBook(
        @Parameter(description = "本ID")
        @PathVariable Long id
    );
}
