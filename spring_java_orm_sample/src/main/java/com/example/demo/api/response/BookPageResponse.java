package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

@Value
public class BookPageResponse {
    @Schema(description = "検索結果")
    @NotNull
    List<BookResponse> content;

    @Schema(description = "ページ番号", type = "integer", minimum = "0")
    int page;

    @Schema(description = "1ページあたりの件数", type = "integer", minimum = "1")
    int size;

    @Schema(description = "総件数", type = "integer", format = "int64", minimum = "0")
    long totalElements;

    @Schema(description = "総ページ数", type = "integer", minimum = "0")
    int totalPages;
}
