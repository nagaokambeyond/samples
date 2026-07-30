package com.example.demo.mybatis.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.mybatis.generator.entity.BookEntity
import com.example.demo.mybatis.generator.entity.BookEntityExample
import com.example.demo.mybatis.generator.entity.StoreEntity
import com.example.demo.mybatis.generator.entity.SupplierEntity
import com.example.demo.mybatis.generator.mapper.BookMapper
import com.example.demo.mybatis.generator.mapper.StoreMapper
import com.example.demo.mybatis.generator.mapper.SupplierMapper
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.time.LocalDate
import java.util.List

internal class PurchaseDataValidatorMybatisTest {
    private var bookMapper: BookMapper? = null
    private var supplierMapper: SupplierMapper? = null
    private var storeMapper: StoreMapper? = null
    private var validator: PurchaseDataValidatorMybatis? = null

    @BeforeEach
    fun setUp() {
        bookMapper = Mockito.mock<BookMapper>(BookMapper::class.java)
        supplierMapper = Mockito.mock<SupplierMapper>(SupplierMapper::class.java)
        storeMapper = Mockito.mock<StoreMapper>(StoreMapper::class.java)
        validator = PurchaseDataValidatorMybatis(bookMapper!!, supplierMapper!!, storeMapper!!)
    }

    @Test
    fun foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000004")
        Mockito.`when`<SupplierEntity?>(supplierMapper!!.selectByPrimaryKey(1L)).thenReturn(SupplierEntity())
        Mockito.`when`<StoreEntity?>(storeMapper!!.selectByPrimaryKey(2L)).thenReturn(StoreEntity())
        Mockito.`when`<MutableList<BookEntity?>?>(
            bookMapper!!.selectByExample(
                ArgumentMatchers.any<BookEntityExample?>(
                    BookEntityExample::class.java
                )
            )
        )
            .thenReturn(List.of<BookEntity?>(book(3L)))
            .thenReturn(List.of<BookEntity?>(book(4L)))

        val result = validator!!.foreignKeyValidate(request)

        Assertions.assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L)
    }

    @Test
    fun foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        val request = createRequest(999L, 2L, "0000000000003")
        Mockito.`when`<SupplierEntity?>(supplierMapper!!.selectByPrimaryKey(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: supplier(id=999)")
        Mockito.verifyNoInteractions(storeMapper, bookMapper)
    }

    @Test
    fun foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        val request = createRequest(1L, 999L, "0000000000003")
        Mockito.`when`<SupplierEntity?>(supplierMapper!!.selectByPrimaryKey(1L)).thenReturn(SupplierEntity())
        Mockito.`when`<StoreEntity?>(storeMapper!!.selectByPrimaryKey(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: store(id=999)")
        Mockito.verifyNoInteractions(bookMapper)
    }

    @Test
    fun foreignKeyValidateThrowsWhenBookDoesNotExist() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000999")
        Mockito.`when`<SupplierEntity?>(supplierMapper!!.selectByPrimaryKey(1L)).thenReturn(SupplierEntity())
        Mockito.`when`<StoreEntity?>(storeMapper!!.selectByPrimaryKey(2L)).thenReturn(StoreEntity())
        Mockito.`when`<MutableList<BookEntity?>?>(
            bookMapper!!.selectByExample(
                ArgumentMatchers.any<BookEntityExample?>(
                    BookEntityExample::class.java
                )
            )
        )
            .thenReturn(List.of<BookEntity?>(book(3L)))
            .thenReturn(mutableListOf<BookEntity?>())

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book(isbn=0000000000999)")
    }

    private fun createRequest(
        supplierId: Long?,
        receivingStoreId: Long?,
        vararg isbns: String?
    ): PurchaseInvoiceCreateRequest {
        val details = isbns.map { isbn ->
            PurchaseInvoiceDetailCreateRequest(
                isbn,
                1000,
                1
            )
        }
        return PurchaseInvoiceCreateRequest(LocalDate.of(2026, 2, 1), supplierId, receivingStoreId, details)
    }

    private fun book(id: Long?): BookEntity {
        val book = BookEntity()
        book.setId(id)
        return book
    }
}
