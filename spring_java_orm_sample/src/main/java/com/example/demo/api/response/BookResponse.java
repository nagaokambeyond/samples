package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class BookResponse{
    @NotNull
    Long id;

    @Schema(description = "タイトル", type = "string")
    @NotNull
    String title;

    @Schema(description = "著者", type = "string")
    String author;

    @Schema(description = "更新日時")
    LocalDateTime updateAt;

    @Schema(description = "バージョン")
    Long version;
}
