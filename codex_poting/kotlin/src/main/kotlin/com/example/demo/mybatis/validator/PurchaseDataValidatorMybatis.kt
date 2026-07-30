package com.example.demo.mybatis.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.mybatis.generator.entity.BookEntityExample
import com.example.demo.mybatis.generator.entity.StoreEntity
import com.example.demo.mybatis.generator.entity.SupplierEntity
import com.example.demo.mybatis.generator.mapper.BookMapper
import com.example.demo.mybatis.generator.mapper.StoreMapper
import com.example.demo.mybatis.generator.mapper.SupplierMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("mybatis")
class PurchaseDataValidatorMybatis(
    private val bookMapper: BookMapper,
    private val supplierMapper: SupplierMapper,
    private val storeMapper: StoreMapper
) {
    fun foreignKeyValidate(request: PurchaseInvoiceCreateRequest): MutableMap<String?, Long?> {
        val supplier = supplierMapper.selectByPrimaryKey(request.supplierId)
        if (Objects.isNull(supplier)) {
            throw ForeignKeyReferenceNotFoundException(SupplierEntity::class.java, request.supplierId)
        }
        val store = storeMapper.selectByPrimaryKey(request.receivingStoreId)
        if (Objects.isNull(store)) {
            throw ForeignKeyReferenceNotFoundException(StoreEntity::class.java, request.receivingStoreId)
        }
        return validateBooks(request.details)
    }

    private fun validateBooks(details: List<PurchaseInvoiceDetailCreateRequest>?): MutableMap<String?, Long?> {
        val bookIdsByIsbn = LinkedHashMap<String?, Long?>()
        details.orEmpty().forEach { detail ->
            val isbn = detail.purchaseInvoiceDetailIsbn
            val example = BookEntityExample()
            example.createCriteria().andIsbnEqualTo(isbn)
            val books = bookMapper.selectByExample(example)
            if (books.isEmpty()) {
                throw ForeignKeyReferenceNotFoundException("book", "isbn", isbn)
            }
            bookIdsByIsbn[isbn] = books.first().getId()
        }
        return bookIdsByIsbn
    }
}
