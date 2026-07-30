package com.example.demo.mybatis.service

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.mybatis.converter.PurchaseOperationConverterMybatis
import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntity
import com.example.demo.mybatis.generator.mapper.BookStockMapper
import com.example.demo.mybatis.generator.mapper.BookStockMovementMapper
import com.example.demo.mybatis.generator.mapper.PurchaseOrderDetailMapper
import com.example.demo.mybatis.generator.mapper.PurchaseOrderMapper
import com.example.demo.mybatis.mapper.BookStockCustomMapper
import com.example.demo.mybatis.validator.PurchaseDataValidatorMybatis
import com.example.demo.service.PurchaseOperationService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("mybatis")
class PurchaseOperationServiceMybatis(
    private val dataValidator: PurchaseDataValidatorMybatis,
    private val purchaseOrderMapper: PurchaseOrderMapper,
    private val purchaseOrderDetailMapper: PurchaseOrderDetailMapper,
    private val bookStockCustomMapper: BookStockCustomMapper,
    private val bookStockMovementMapper: BookStockMovementMapper,
    private val bookStockMapper: BookStockMapper,
    private val converter: PurchaseOperationConverterMybatis
) : PurchaseOperationService {
    @RetryableOnLockFailure
    @Transactional
    override fun create(request: PurchaseInvoiceCreateRequest): PurchaseInvoiceResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        val bookIdsByIsbn = dataValidator.foreignKeyValidate(request)
        val now = LocalDateTime.now()
        val details = converter.toPurchaseInvoiceDetails(request, bookIdsByIsbn, now)
        val amount =
            details.stream().mapToLong { obj: PurchaseOrderDetailEntity? -> obj!!.getPurchaseInvoiceDetailAmount() }
                .sum()
        val purchaseInvoice = converter.toPurchaseInvoice(request, amount, now)
        purchaseOrderMapper.insert(purchaseInvoice)
        details.filterNotNull().forEach { purchaseInvoiceDetail ->
            purchaseInvoiceDetail!!.setPurchaseInvoiceId(purchaseInvoice.getId())
            purchaseOrderDetailMapper.insert(purchaseInvoiceDetail)
            bookStockMovementMapper.insert(converter.toBookStockMovement(purchaseInvoice, purchaseInvoiceDetail, now))
            val bookStock = bookStockCustomMapper.selectByStoreIdAndBookIdWithWriteLock(
                purchaseInvoice.getReceivingStoreId(),
                purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId()
            )
            if (Objects.isNull(bookStock)) {
                val stock = converter.toBookStock(purchaseInvoice.getReceivingStoreId(), purchaseInvoiceDetail, now)
                bookStockMapper.insert(stock)
                return@forEach
            }
            val quantity =
                bookStock!!.getBookStockQuantity() + purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity()
            bookStock.setBookStockQuantity(quantity)
            bookStock.setUpdateAt(now)
            bookStock.setVersion(bookStock.getVersion() + 1)
            bookStockMapper.updateByPrimaryKey(bookStock)
        }
        return converter.toResponse(purchaseInvoice, details)
    }
}
