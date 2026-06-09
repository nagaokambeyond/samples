package com.example.demo.api;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/books")
@Tag(name = "Books", description = "本API")
public interface BookApi {
    @GetMapping()
    @Operation(summary = "本全取得")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功")
    })
    ResponseEntity<List<BookResponse>> getBookAll();

    @GetMapping("/{id}")
    @Operation(summary = "本取得")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "リクエストエラー", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
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
        @ApiResponse(responseCode = "400", description = "リクエストエラー", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
    })
    BookPageResponse getBookSearch(
        @Parameter(description = "タイトル")
        @RequestParam @NotBlank String title,
        @Parameter(description = "発売日付From")
        @RequestParam(required = false) LocalDate releaseDateFrom,
        @Parameter(description = "発売日付To")
        @RequestParam(required = false) LocalDate releaseDateTo,
        @Parameter(description = "ページ番号（0始まり）")
        @RequestParam @NotNull @Min(0) Integer page,
        @Parameter(description = "1ページあたりの件数")
        @RequestParam @NotNull @Min(1) Integer size
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
    })
    void deleteBook(
        @Parameter(description = "本ID")
        @PathVariable Long id
    );
}
