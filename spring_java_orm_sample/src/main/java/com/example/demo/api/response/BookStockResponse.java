package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class BookStockResponse {
    @Schema(description = "本在庫ID", type = "integer", format = "int64")
    @NotNull
    Long id;

    @Schema(description = "本在庫店舗ID", type = "integer", format = "int64")
    @NotNull
    Long bookStockStoreId;

    @Schema(description = "本在庫店舗名", type = "string")
    @NotNull
    String storeName;

    @Schema(description = "本在庫数量", type = "integer")
    @NotNull
    Integer bookStockQuantity;
}
