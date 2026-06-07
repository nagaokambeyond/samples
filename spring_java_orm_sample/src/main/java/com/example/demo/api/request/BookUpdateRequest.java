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

    @PositiveOrZero
    @NotNull
    @Schema(description = "バージョン", type = "integer", format = "int64")
    Long version;
}
