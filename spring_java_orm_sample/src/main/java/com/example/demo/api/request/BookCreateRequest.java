package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class BookCreateRequest {
    @NotNull
    @Size(min = 1, max = 100)
    @Schema(description = "タイトル", type = "string")
    String title;

    @Size(max = 200)
    @Schema(description = "著者", type = "string")
    String author;
}
