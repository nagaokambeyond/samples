package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class BookResponse{
    @NotNull
    Long id;

    @Schema(description = "タイトル", type = "string")
    @NotNull
    String title;

    @Schema(description = "著者", type = "string")
    String author;
}
