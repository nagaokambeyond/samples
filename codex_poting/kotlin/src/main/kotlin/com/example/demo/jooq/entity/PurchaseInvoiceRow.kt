package com.example.demo.jooq.entity

import java.time.LocalDate
import java.time.LocalDateTime

data class PurchaseInvoiceRow(
    val id: Long?,
    val purchaseInvoiceType: Int?,
    val returnPurchaseInvoiceId: Long?,
    val purchaseInvoiceDate: LocalDate?,
    val supplierId: Long?,
    val receivingStoreId: Long?,
    val purchaseInvoiceAmount: Long?,
    val updateAt: LocalDateTime?,
    val version: Long?
)
