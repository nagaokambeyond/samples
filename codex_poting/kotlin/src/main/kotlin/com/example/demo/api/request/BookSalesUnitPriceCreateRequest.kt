package com.example.demo.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class BookSalesUnitPriceCreateRequest(
    @field:NotNull
    @field:Min(1)
    @field:Max(10000)
    @field:Schema(description = "販売単価", type = "integer", example = "1500")
    val salesUnitPrice: Int?,

    @field:NotNull
    @field:Future
    @field:Schema(description = "有効開始日", type = "string", format = "date")
    val effectiveFrom: LocalDate?
)
