package com.example.demo.api.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

class PurchaseInvoiceDetailResponse {
    @field:Schema(description = "仕入伝票明細ID", type = "integer", format = "int64")
    @field:NotNull
    var id: Long? = null

    @field:Schema(description = "仕入伝票ID", type = "integer", format = "int64")
    @field:NotNull
    var purchaseInvoiceId: Long? = null

    @field:Schema(description = "仕入伝票明細本ID", type = "integer", format = "int64")
    @field:NotNull
    var purchaseInvoiceDetailBookId: Long? = null

    @field:Schema(description = "仕入伝票明細単価")
    @field:NotNull
    var purchaseInvoiceDetailUnitPrice: Int? = null

    @field:Schema(description = "仕入伝票明細数量")
    @field:NotNull
    var purchaseInvoiceDetailQuantity: Int? = null

    @field:Schema(description = "仕入伝票明細金額", type = "integer", format = "int64")
    @field:NotNull
    var purchaseInvoiceDetailAmount: Long? = null

    @field:Schema(description = "更新日時", type = "string", format = "date-time")
    @field:NotNull
    var updateAt: LocalDateTime? = null

    @field:Schema(description = "バージョン", type = "integer", format = "int64")
    @field:NotNull
    var version: Long? = null
}
