package com.example.demo.doma.converter

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceDetailResponse
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.data.domain.BookStockMovementSourceType
import com.example.demo.data.domain.BookStockMovementType
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.doma.generator.entity.BookStock
import com.example.demo.doma.generator.entity.BookStockMovement
import com.example.demo.doma.generator.entity.PurchaseInvoice
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Profile("doma")
class PurchaseOperationConverterDoma(private val modelMapper: ModelMapper) {
    fun toPurchaseInvoiceDetails(
        request: PurchaseInvoiceCreateRequest,
        bookIdsByIsbn: MutableMap<String?, Long?>,
        now: LocalDateTime?
    ): MutableList<PurchaseInvoiceDetail?> {
        return request.details!!.stream()
            .map<PurchaseInvoiceDetail?> { purchaseInvoiceDetail: PurchaseInvoiceDetailCreateRequest? ->
                val row =
                    modelMapper.map<PurchaseInvoiceDetail>(purchaseInvoiceDetail, PurchaseInvoiceDetail::class.java)
                row.setPurchaseInvoiceDetailBookId(bookIdsByIsbn.get(purchaseInvoiceDetail!!.purchaseInvoiceDetailIsbn))
                val amount =
                    (row.getPurchaseInvoiceDetailUnitPrice().toLong() * row.getPurchaseInvoiceDetailQuantity().toLong())
                row.setPurchaseInvoiceDetailAmount(amount)
                row.setCreateAt(now)
                row.setUpdateAt(now)
                row
            }.toList()
    }

    fun toPurchaseInvoice(request: PurchaseInvoiceCreateRequest?, amount: Long, now: LocalDateTime?): PurchaseInvoice {
        val result = modelMapper.map<PurchaseInvoice>(request, PurchaseInvoice::class.java)
        result.setId(null)
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE)
        result.setPurchaseInvoiceAmount(amount)
        result.setCreateAt(now)
        result.setUpdateAt(now)
        return result
    }

    fun toBookStock(storeId: Long?, purchaseInvoiceDetail: PurchaseInvoiceDetail, now: LocalDateTime?): BookStock {
        val result = BookStock()
        result.setId(null)
        result.setBookStockStoreId(storeId)
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId())
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity())
        result.setCreateAt(now)
        result.setUpdateAt(now)
        return result
    }

    fun toBookStockMovement(
        purchaseInvoice: PurchaseInvoice,
        purchaseInvoiceDetail: PurchaseInvoiceDetail,
        now: LocalDateTime?
    ): BookStockMovement {
        val result = BookStockMovement()
        result.setStoreId(purchaseInvoice.getReceivingStoreId())
        result.setBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId())
        result.setMovementType(BookStockMovementType.PURCHASE)
        result.setQuantityDelta(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity())
        result.setSourceType(BookStockMovementSourceType.PURCHASE_INVOICE)
        result.setSourceId(purchaseInvoice.getId())
        result.setSourceDetailId(purchaseInvoiceDetail.getId())
        result.setMovementDate(purchaseInvoice.getPurchaseInvoiceDate())
        result.setCreateAt(now)
        result.setUpdateAt(now)
        return result
    }

    fun toRespose(
        purchaseInvoice: PurchaseInvoice?,
        details: MutableList<PurchaseInvoiceDetail?>
    ): PurchaseInvoiceResponse {
        val list = details.filterNotNull().map { row: PurchaseInvoiceDetail ->
            modelMapper.map<PurchaseInvoiceDetailResponse>(
                row,
                PurchaseInvoiceDetailResponse::class.java
            )
        }
        val response = modelMapper.map<PurchaseInvoiceResponse>(purchaseInvoice, PurchaseInvoiceResponse::class.java)
        response.detail = list
        return response
    }
}
