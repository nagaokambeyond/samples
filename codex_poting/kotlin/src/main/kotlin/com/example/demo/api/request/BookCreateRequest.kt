package com.example.demo.api.request

import com.example.demo.api.annotation.Isbn
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class BookCreateRequest(
    @field:NotNull
    @field:Size(min = 1, max = 100)
    @field:Schema(description = "タイトル", type = "string")
    val title: String?,

    @field:Size(max = 200)
    @field:Schema(description = "著者", type = "string")
    val author: String?,

    @field:NotNull
    @field:Schema(description = "発売日付", type = "string", format = "date")
    val releaseDate: LocalDate?,

    @field:NotNull
    @field:Schema(description = "出版社ID", type = "integer", format = "int64")
    val publisherId: Long?,

    @field:NotNull
    @field:Schema(description = "ジャンルID", type = "integer", format = "int64")
    val genreId: Long?,

    @field:NotNull
    @field:Isbn
    @field:Schema(description = "ISBN", type = "string", example = "9784000000000")
    val isbn: String?,

    @field:NotNull
    @field:Min(1)
    @field:Max(10000)
    @field:Schema(description = "販売単価", type = "integer", example = "1200")
    val salesUnitPrice: Int?
)
