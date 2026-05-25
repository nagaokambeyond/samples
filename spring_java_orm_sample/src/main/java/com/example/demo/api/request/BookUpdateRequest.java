package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class BookUpdateRequest{
    @NotNull
    Long id;

    @NotNull @Size(min = 1, max = 100)
    @Schema(description = "タイトル", type = "string")
    String title;

    @Schema(description = "著者", type = "string")
    @Size(max = 200)
    String author;

    @PositiveOrZero
    @NotNull
    @Schema(description = "バージョン")
    Long version;
}
