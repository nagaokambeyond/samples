package com.example.demo.api.request;

import com.example.demo.api.annotation.Isbn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BookCreateRequest {
    @NotNull
    @Size(min = 1, max = 100)
    @Schema(description = "タイトル", type = "string")
    String title;

    @Size(max = 200)
    @Schema(description = "著者", type = "string")
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
    @Isbn
    @Schema(description = "ISBN", type = "string", example = "9784000000000")
    String isbn;
}
