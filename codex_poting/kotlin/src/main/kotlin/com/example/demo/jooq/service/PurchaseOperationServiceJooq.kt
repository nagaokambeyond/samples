package com.example.demo.jooq.service

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.jooq.converter.PurchaseOperationConverterJooq
import com.example.demo.jooq.dsl.PurchaseOperationDsl
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow
import com.example.demo.jooq.validator.PurchaseDataValidatorJooq
import com.example.demo.service.PurchaseOperationService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Profile("jooq")
class PurchaseOperationServiceJooq(
    private val purchaseOperationDsl: PurchaseOperationDsl,
    private val dataValidator: PurchaseDataValidatorJooq,
    private val converter: PurchaseOperationConverterJooq
) : PurchaseOperationService {
    @RetryableOnLockFailure
    @Transactional
    override fun create(request: PurchaseInvoiceCreateRequest): PurchaseInvoiceResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        val bookIdsByIsbn = dataValidator.foreignKeyValidate(request)
        val now = LocalDateTime.now()
        val amount = converter.calculateAmount(request)
        val purchaseInvoice = purchaseOperationDsl.insertPurchaseInvoice(request, amount, now)
        val details = ArrayList<PurchaseInvoiceDetailRow?>()
        request.details!!.filterNotNull().forEach { detailRequest ->
            val detailAmount = converter.calculateAmount(detailRequest)
            val bookId = bookIdsByIsbn.get(detailRequest.purchaseInvoiceDetailIsbn)
            val detail = purchaseOperationDsl.insertPurchaseInvoiceDetail(
                purchaseInvoice.id,
                detailRequest,
                bookId,
                detailAmount,
                now
            )
            details.add(detail)
            purchaseOperationDsl.insertBookStockMovement(
                purchaseInvoice.receivingStoreId,
                purchaseInvoice,
                detail,
                now
            )
            purchaseOperationDsl.addStockQuantity(purchaseInvoice.receivingStoreId, detail, now)
        }
        return converter.toResponse(purchaseInvoice, details)
    }
}
