package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BookUpdateRequest{
    @NotNull
    @Schema(description = "本ID", type = "integer", format = "int64")
    Long id;

    @NotNull @Size(min = 1, max = 100)
    @Schema(description = "タイトル", type = "string")
    String title;

    @Schema(description = "著者", type = "string")
    @Size(max = 200)
    String author;

    @NotNull
    @Schema(description = "発売日付", type = "string", format ="date")
    LocalDate releaseDate;

    @NotNull
    @Schema(description = "出版社ID", type = "integer", format ="int64")
    Long publisherId;

    @NotNull
    @Schema(description = "ジャンルID", type = "integer", format ="int64")
    Long genreId;

    @NotNull
    @Size(min = 13, max = 13)
    @Pattern(regexp = "\\d{13}")
    @Schema(description = "ISBN", type = "string", example = "9784000000000")
    String isbn;

    @PositiveOrZero
    @NotNull
    @Schema(description = "バージョン", type = "integer", format = "int64")
    Long version;
}
