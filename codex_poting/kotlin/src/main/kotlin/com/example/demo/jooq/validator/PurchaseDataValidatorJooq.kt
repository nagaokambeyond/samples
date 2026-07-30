package com.example.demo.jooq.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.jooq.dsl.BookDsl
import com.example.demo.jooq.dsl.StoreDsl
import com.example.demo.jooq.dsl.SupplierDsl
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer

@Component
@Profile("jooq")
class PurchaseDataValidatorJooq(
    private val bookDsl: BookDsl,
    private val storeDsl: StoreDsl,
    private val supplierDsl: SupplierDsl
) {
    fun foreignKeyValidate(request: PurchaseInvoiceCreateRequest): MutableMap<String?, Long?> {
        if (!supplierDsl.exists(request.supplierId)) {
            throw ForeignKeyReferenceNotFoundException("supplier", request.supplierId)
        }
        if (!storeDsl.exists(request.receivingStoreId)) {
            throw ForeignKeyReferenceNotFoundException("store", request.receivingStoreId)
        }
        val bookIdsByIsbn = LinkedHashMap<String?, Long?>()
        request.details!!.forEach(Consumer { detail: PurchaseInvoiceDetailCreateRequest? ->
            val isbn = detail!!.purchaseInvoiceDetailIsbn
            val bookId = bookDsl.selectIdByIsbn(isbn)
            if (Objects.isNull(bookId)) {
                throw ForeignKeyReferenceNotFoundException("book", "isbn", isbn)
            }
            bookIdsByIsbn.put(isbn, bookId)
        })
        return bookIdsByIsbn
    }
}
