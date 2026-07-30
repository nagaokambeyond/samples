package com.example.demo.api.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class BookPageResponse {
    @field:Schema(description = "検索結果")
    @field:NotNull
    var content: List<BookResponse>? = null

    @field:Schema(description = "ページ番号", type = "integer", minimum = "0")
    var page: Int = 0

    @field:Schema(description = "1ページあたりの件数", type = "integer", minimum = "1")
    var size: Int = 0

    @field:Schema(description = "総件数", type = "integer", format = "int64", minimum = "0")
    var totalElements: Long = 0

    @field:Schema(description = "総ページ数", type = "integer", minimum = "0")
    var totalPages: Int = 0
}
