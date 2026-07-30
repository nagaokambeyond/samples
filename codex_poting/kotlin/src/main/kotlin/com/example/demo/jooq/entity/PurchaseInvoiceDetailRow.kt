package com.example.demo.jooq.entity

data class PurchaseInvoiceDetailRow(
    val id: Long?,
    val purchaseInvoiceId: Long?,
    val purchaseInvoiceDetailBookId: Long?,
    val purchaseInvoiceDetailUnitPrice: Int?,
    val purchaseInvoiceDetailQuantity: Int?,
    val purchaseInvoiceDetailAmount: Long?,
    val version: Long?
)
