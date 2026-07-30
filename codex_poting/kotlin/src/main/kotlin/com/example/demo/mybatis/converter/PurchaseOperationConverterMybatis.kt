package com.example.demo.mybatis.converter

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceDetailResponse
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.data.domain.BookStockMovementSourceType
import com.example.demo.data.domain.BookStockMovementType
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.mybatis.generator.entity.BookStockEntity
import com.example.demo.mybatis.generator.entity.BookStockMovementEntity
import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntity
import com.example.demo.mybatis.generator.entity.PurchaseOrderEntity
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Profile("mybatis")
class PurchaseOperationConverterMybatis(private val modelMapper: ModelMapper) {
    fun toPurchaseInvoiceDetails(
        request: PurchaseInvoiceCreateRequest,
        bookIdsByIsbn: MutableMap<String?, Long?>,
        now: LocalDateTime?
    ): MutableList<PurchaseOrderDetailEntity?> {
        return request.details!!.stream()
            .map<PurchaseOrderDetailEntity?> { purchaseInvoiceDetail: PurchaseInvoiceDetailCreateRequest? ->
                val row = modelMapper.map<PurchaseOrderDetailEntity>(
                    purchaseInvoiceDetail,
                    PurchaseOrderDetailEntity::class.java
                )
                row.setPurchaseInvoiceDetailBookId(bookIdsByIsbn.get(purchaseInvoiceDetail!!.purchaseInvoiceDetailIsbn))
                val amount = row.getPurchaseInvoiceDetailUnitPrice().toLong() * row.getPurchaseInvoiceDetailQuantity()
                row.setPurchaseInvoiceDetailAmount(amount)
                row.setCreateAt(now)
                row.setUpdateAt(now)
                row.setVersion(1L)
                row
            }.toList()
    }

    fun toPurchaseInvoice(
        request: PurchaseInvoiceCreateRequest?,
        amount: Long,
        now: LocalDateTime?
    ): PurchaseOrderEntity {
        val result = modelMapper.map<PurchaseOrderEntity>(request, PurchaseOrderEntity::class.java)
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE)
        result.setPurchaseInvoiceAmount(amount)
        result.setCreateAt(now)
        result.setUpdateAt(now)
        result.setVersion(1L)
        return result
    }

    fun toBookStock(
        storeId: Long?,
        purchaseInvoiceDetail: PurchaseOrderDetailEntity,
        now: LocalDateTime?
    ): BookStockEntity {
        val result = BookStockEntity()
        result.setBookStockStoreId(storeId)
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId())
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity())
        result.setCreateAt(now)
        result.setUpdateAt(now)
        result.setVersion(1L)
        return result
    }

    fun toBookStockMovement(
        purchaseInvoice: PurchaseOrderEntity,
        purchaseInvoiceDetail: PurchaseOrderDetailEntity,
        now: LocalDateTime?
    ): BookStockMovementEntity {
        val result = BookStockMovementEntity()
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
        result.setVersion(1L)
        return result
    }

    fun toResponse(
        purchaseInvoice: PurchaseOrderEntity?,
        details: MutableList<PurchaseOrderDetailEntity?>
    ): PurchaseInvoiceResponse {
        val list = details.filterNotNull().map { row: PurchaseOrderDetailEntity ->
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
