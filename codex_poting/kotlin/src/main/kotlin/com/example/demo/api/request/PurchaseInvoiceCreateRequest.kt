package com.example.demo.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class PurchaseInvoiceCreateRequest(
    @field:Schema(description = "仕入伝票日付", type = "string", format = "date")
    @field:NotNull
    val purchaseInvoiceDate: LocalDate?,

    @field:Schema(description = "仕入先ID", type = "integer", format = "int64")
    @field:NotNull
    val supplierId: Long?,

    @field:Schema(description = "入庫店舗ID", type = "integer", format = "int64")
    @field:NotNull
    val receivingStoreId: Long?,

    @field:Schema(description = "仕入伝票明細")
    @field:Valid
    @field:NotEmpty
    @field:NotNull
    @field:Size(max = 10)
    val details: List<PurchaseInvoiceDetailCreateRequest>?
)
