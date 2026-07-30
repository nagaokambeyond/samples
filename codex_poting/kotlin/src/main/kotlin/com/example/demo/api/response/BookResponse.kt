package com.example.demo.api.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

class BookResponse {
    @field:Schema(description = "本ID", type = "integer", format = "int64")
    @field:NotNull
    var id: Long? = null

    @field:Schema(description = "タイトル", type = "string")
    @field:NotNull
    var title: String? = null

    @field:Schema(description = "著者", type = "string")
    var author: String? = null

    @field:NotNull
    @field:Schema(description = "発売日付", type = "string", format = "date")
    var releaseDate: LocalDate? = null

    @field:NotNull
    @field:Schema(description = "出版社ID", type = "integer", format = "int64")
    var publisherId: Long? = null

    @field:NotNull
    @field:Schema(description = "出版社名", type = "string")
    var publisherName: String? = null

    @field:NotNull
    @field:Schema(description = "ジャンルID", type = "integer", format = "int64")
    var genreId: Long? = null

    @field:NotNull
    @field:Schema(description = "ジャンル名", type = "string")
    var genreName: String? = null

    @field:NotNull
    @field:Schema(description = "ISBN", type = "string")
    var isbn: String? = null

    @field:NotNull
    @field:Schema(description = "販売単価", type = "integer")
    var salesUnitPrice: Int? = null

    @field:Schema(description = "更新日時", type = "string", format = "date-time")
    @field:NotNull
    var updateAt: LocalDateTime? = null

    @field:Schema(description = "バージョン", type = "integer", format = "int64")
    @field:NotNull
    var version: Long? = null

    @field:Schema(description = "本在庫リスト")
    @field:NotNull
    var bookStockList: List<BookStockResponse>? = null
}
