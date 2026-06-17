package com.example.demo.api;

import com.example.demo.api.request.BookCreateRequest;
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
        @ApiResponse(responseCode = "404", description = "データなし", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "リクエストエラー", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookResponse createBook(
        @RequestBody @Valid @NotNull BookCreateRequest request
    );

    @PostMapping("/update")
    @Operation(summary = "本更新")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "リクエストエラー", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "データなし", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "更新競合", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class)))
    })
    BookResponse updateBook(
        @RequestBody @Valid @NotNull BookUpdateRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(summary = "本削除")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "リクエストエラー", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "データなし", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "更新競合", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class)))
    })
    void deleteBook(
        @Parameter(description = "本ID")
        @PathVariable Long id
    );
}
