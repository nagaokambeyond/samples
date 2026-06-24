package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookResponse{
    @Schema(description = "本ID", type = "integer", format = "int64")
    @NotNull
    Long id;

    @Schema(description = "タイトル", type = "string")
    @NotNull
    String title;

    @Schema(description = "著者", type = "string")
    String author;

    @NotNull
    @Schema(description = "発売日付", type = "string", format ="date")
    LocalDate releaseDate;

    @NotNull
    @Schema(description = "出版社ID", type = "integer", format ="int64")
    Long publisherId;

    @NotNull
    @Schema(description = "出版社名", type = "string")
    String publisherName;

    @NotNull
    @Schema(description = "ジャンルID", type = "integer", format ="int64")
    Long genreId;

    @NotNull
    @Schema(description = "ジャンル名", type = "string")
    String genreName;

    @Schema(description = "更新日時", type = "string", format = "date-time")
    @NotNull
    LocalDateTime updateAt;

    @Schema(description = "バージョン", type = "integer", format = "int64")
    @NotNull
    Long version;

    @Schema(description = "本在庫リスト")
    @NotNull
    List<BookStockResponse> bookStockList;
}
