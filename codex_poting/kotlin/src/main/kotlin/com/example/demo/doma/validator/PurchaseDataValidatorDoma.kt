package com.example.demo.doma.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.doma.dao.BookCustomDao
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao
import com.example.demo.doma.generator.dao.StoreDao
import com.example.demo.doma.generator.dao.SupplierDao
import com.example.demo.doma.generator.entity.PurchaseInvoice
import com.example.demo.doma.generator.entity.Store
import com.example.demo.doma.generator.entity.Supplier
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("doma")
class PurchaseDataValidatorDoma(
    private val bookCustomDao: BookCustomDao,
    private val purchaseInvoiceDao: PurchaseInvoiceDao,
    private val supplierDao: SupplierDao,
    private val storeDao: StoreDao
) {
    fun foreignKeyValidate(request: PurchaseInvoiceCreateRequest): MutableMap<String?, Long?> {
        val supplier = supplierDao.selectById(request.supplierId)
        if (Objects.isNull(supplier)) {
            throw ForeignKeyReferenceNotFoundException(Supplier::class.java, request.supplierId)
        }
        val store = storeDao.selectById(request.receivingStoreId)
        if (Objects.isNull(store)) {
            throw ForeignKeyReferenceNotFoundException(Store::class.java, request.receivingStoreId)
        }
        return validateBooks(request.details)
    }

    fun returnPurchaseInvoiceIdValidate(returnPurchaseInvoiceId: Long?) {
        if (Objects.isNull(returnPurchaseInvoiceId)) {
            return
        }
        val purchaseInvoice = purchaseInvoiceDao.selectById(returnPurchaseInvoiceId)
        if (Objects.isNull(purchaseInvoice)) {
            throw ForeignKeyReferenceNotFoundException(PurchaseInvoice::class.java, returnPurchaseInvoiceId)
        }
        if (purchaseInvoice!!.getPurchaseInvoiceType() != PurchaseInvoiceType.PURCHASE) {
            throw ForeignKeyReferenceNotFoundException(PurchaseInvoice::class.java, returnPurchaseInvoiceId)
        }
    }

    private fun validateBooks(details: List<PurchaseInvoiceDetailCreateRequest>?): MutableMap<String?, Long?> {
        val bookIdsByIsbn = LinkedHashMap<String?, Long?>()
        details.orEmpty().forEach { detail ->
            val isbn = detail.purchaseInvoiceDetailIsbn
            val book = bookCustomDao.selectByIsbn(isbn)
            if (Objects.isNull(book)) {
                throw ForeignKeyReferenceNotFoundException("book", "isbn", isbn)
            }
            bookIdsByIsbn[isbn] = book!!.getId()
        }
        return bookIdsByIsbn
    }
}
