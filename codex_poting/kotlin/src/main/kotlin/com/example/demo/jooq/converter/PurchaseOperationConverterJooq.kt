package com.example.demo.jooq.converter

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceDetailResponse
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow
import com.example.demo.jooq.entity.PurchaseInvoiceRow
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class PurchaseOperationConverterJooq {
    fun toResponse(
        purchaseInvoice: PurchaseInvoiceRow,
        details: MutableList<PurchaseInvoiceDetailRow?>
    ): PurchaseInvoiceResponse {
        val response = PurchaseInvoiceResponse()
        response.id = purchaseInvoice.id
        response.purchaseInvoiceType = PurchaseInvoiceType.of(purchaseInvoice.purchaseInvoiceType!!)
        response.returnPurchaseInvoiceId = purchaseInvoice.returnPurchaseInvoiceId
        response.purchaseInvoiceDate = purchaseInvoice.purchaseInvoiceDate
        response.supplierId = purchaseInvoice.supplierId
        response.receivingStoreId = purchaseInvoice.receivingStoreId
        response.purchaseInvoiceAmount = purchaseInvoice.purchaseInvoiceAmount
        response.updateAt = purchaseInvoice.updateAt
        response.version = purchaseInvoice.version
        response.detail = details.filterNotNull().map { toResponse(it) }
        return response
    }

    fun calculateAmount(request: PurchaseInvoiceCreateRequest): Long {
        return request.details!!.stream()
            .mapToLong { detail: PurchaseInvoiceDetailCreateRequest? -> this.calculateAmount(detail!!) }
            .sum()
    }

    fun calculateAmount(detail: PurchaseInvoiceDetailCreateRequest): Long {
        return detail.purchaseInvoiceDetailUnitPrice!!.toLong() * detail.purchaseInvoiceDetailQuantity!!.toLong()
    }

    private fun toResponse(row: PurchaseInvoiceDetailRow): PurchaseInvoiceDetailResponse {
        val response = PurchaseInvoiceDetailResponse()
        response.id = row.id
        response.purchaseInvoiceId = row.purchaseInvoiceId
        response.purchaseInvoiceDetailBookId = row.purchaseInvoiceDetailBookId
        response.purchaseInvoiceDetailUnitPrice = row.purchaseInvoiceDetailUnitPrice
        response.purchaseInvoiceDetailQuantity = row.purchaseInvoiceDetailQuantity
        response.purchaseInvoiceDetailAmount = row.purchaseInvoiceDetailAmount
        response.version = row.version
        return response
    }
}
