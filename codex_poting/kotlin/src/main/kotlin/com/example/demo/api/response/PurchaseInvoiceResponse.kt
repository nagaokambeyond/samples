package com.example.demo.api.response

import com.example.demo.data.domain.PurchaseInvoiceType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

class PurchaseInvoiceResponse {
    @field:Schema(description = "仕入伝票ID", type = "integer", format = "int64")
    @field:NotNull
    var id: Long? = null

    @field:Schema(description = "仕入伝票種別")
    @field:NotNull
    var purchaseInvoiceType: PurchaseInvoiceType? = null

    @field:Schema(description = "返品元仕入伝票ID", type = "integer", format = "int64")
    var returnPurchaseInvoiceId: Long? = null

    @field:Schema(description = "仕入伝票日付", type = "string", format = "date")
    @field:NotNull
    var purchaseInvoiceDate: LocalDate? = null

    @field:Schema(description = "仕入先ID", type = "integer", format = "int64")
    @field:NotNull
    var supplierId: Long? = null

    @field:Schema(description = "入庫店舗ID", type = "integer", format = "int64")
    @field:NotNull
    var receivingStoreId: Long? = null

    @field:Schema(description = "仕入伝票金額", type = "integer", format = "int64")
    @field:NotNull
    var purchaseInvoiceAmount: Long? = null

    @field:Schema(description = "更新日時", type = "string", format = "date-time")
    @field:NotNull
    var updateAt: LocalDateTime? = null

    @field:Schema(description = "バージョン", type = "integer", format = "int64")
    @field:NotNull
    var version: Long? = null

    @field:Schema(description = "仕入伝票明細")
    @field:NotEmpty
    var detail: List<PurchaseInvoiceDetailResponse>? = null
}
