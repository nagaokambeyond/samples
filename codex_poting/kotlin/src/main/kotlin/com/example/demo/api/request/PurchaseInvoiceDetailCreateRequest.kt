package com.example.demo.api.request

import com.example.demo.api.annotation.Isbn
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class PurchaseInvoiceDetailCreateRequest(
    @field:Schema(description = "仕入伝票明細ISBN", type = "string", example = "0000000000001")
    @field:NotNull
    @field:Isbn
    val purchaseInvoiceDetailIsbn: String?,

    @field:Schema(description = "仕入伝票明細単価")
    @field:Max(10000)
    @field:Min(1)
    @field:NotNull
    val purchaseInvoiceDetailUnitPrice: Int?,

    @field:Schema(description = "仕入伝票明細数量")
    @field:Max(1000)
    @field:Min(1)
    @field:NotNull
    val purchaseInvoiceDetailQuantity: Int?
)
