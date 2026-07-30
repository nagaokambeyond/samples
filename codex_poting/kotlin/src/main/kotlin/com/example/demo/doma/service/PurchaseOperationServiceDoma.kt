package com.example.demo.doma.service

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.response.PurchaseInvoiceResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.doma.converter.PurchaseOperationConverterDoma
import com.example.demo.doma.dao.BookStockCustomDao
import com.example.demo.doma.generator.dao.BookStockDao
import com.example.demo.doma.generator.dao.BookStockMovementDao
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao
import com.example.demo.doma.generator.dao.PurchaseInvoiceDetailDao
import com.example.demo.doma.generator.entity.BookStock
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail
import com.example.demo.doma.validator.PurchaseDataValidatorDoma
import com.example.demo.service.PurchaseOperationService
import org.seasar.doma.jdbc.OptimisticLockException
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("doma")
@Primary
class PurchaseOperationServiceDoma(
    private val dataValidator: PurchaseDataValidatorDoma,
    private val purchaseInvoiceDao: PurchaseInvoiceDao,
    private val purchaseInvoiceDetailDao: PurchaseInvoiceDetailDao,
    private val bookStockCustomDao: BookStockCustomDao,
    private val bookStockMovementDao: BookStockMovementDao,
    private val bookStockDao: BookStockDao,
    private val converter: PurchaseOperationConverterDoma
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
            details.stream().mapToLong { obj: PurchaseInvoiceDetail? -> obj!!.getPurchaseInvoiceDetailAmount() }.sum()
        val purchaseInvoice = converter.toPurchaseInvoice(request, amount, now)
        purchaseInvoiceDao.insert(purchaseInvoice)
        details.filterNotNull().forEach { purchaseInvoiceDetail ->
            purchaseInvoiceDetail!!.setPurchaseInvoiceId(purchaseInvoice.getId())
            purchaseInvoiceDetailDao.insert(purchaseInvoiceDetail)
            bookStockMovementDao.insert(converter.toBookStockMovement(purchaseInvoice, purchaseInvoiceDetail, now))
            val bookStock = bookStockCustomDao.selectByStoreIdAndBookIdWithWriteLock(
                purchaseInvoice.getReceivingStoreId(),
                purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId()
            )
            if (Objects.isNull(bookStock)) {
                val stock = converter.toBookStock(purchaseInvoice.getReceivingStoreId(), purchaseInvoiceDetail, now)
                bookStockDao.insert(stock)
                return@forEach
            }
            val quantity =
                bookStock!!.getBookStockQuantity() + purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity()
            bookStock.setBookStockQuantity(quantity)
            bookStock.setUpdateAt(now)
            try {
                bookStockDao.update(bookStock)
            } catch (ex: OptimisticLockException) {
                throw ObjectOptimisticLockingFailureException(BookStock::class.java, bookStock.getId(), ex)
            }
        }
        return converter.toRespose(purchaseInvoice, details)
    }
}
