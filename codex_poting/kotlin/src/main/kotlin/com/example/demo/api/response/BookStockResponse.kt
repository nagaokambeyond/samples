package com.example.demo.api.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class BookStockResponse {
    @field:Schema(description = "本在庫ID", type = "integer", format = "int64")
    @field:NotNull
    var id: Long? = null

    @field:Schema(description = "本在庫店舗ID", type = "integer", format = "int64")
    @field:NotNull
    var bookStockStoreId: Long? = null

    @field:Schema(description = "本在庫店舗名", type = "string")
    @field:NotNull
    var storeName: String? = null

    @field:Schema(description = "本在庫数量", type = "integer")
    @field:NotNull
    var bookStockQuantity: Int? = null
}
