package com.example.demo.jooq.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.jooq.dsl.BookDsl
import com.example.demo.jooq.dsl.StoreDsl
import com.example.demo.jooq.dsl.SupplierDsl
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.util.List

internal class PurchaseDataValidatorJooqTest {
    private var bookDsl: BookDsl? = null
    private var storeDsl: StoreDsl? = null
    private var supplierDsl: SupplierDsl? = null
    private var validator: PurchaseDataValidatorJooq? = null

    @BeforeEach
    fun setUp() {
        bookDsl = Mockito.mock<BookDsl>(BookDsl::class.java)
        storeDsl = Mockito.mock<StoreDsl>(StoreDsl::class.java)
        supplierDsl = Mockito.mock<SupplierDsl>(SupplierDsl::class.java)
        validator = PurchaseDataValidatorJooq(bookDsl!!, storeDsl!!, supplierDsl!!)
    }

    @Test
    fun foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000004")
        Mockito.`when`<Boolean?>(supplierDsl!!.exists(1L)).thenReturn(true)
        Mockito.`when`<Boolean?>(storeDsl!!.exists(2L)).thenReturn(true)
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("0000000000003")).thenReturn(3L)
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("0000000000004")).thenReturn(4L)

        val result = validator!!.foreignKeyValidate(request)

        Assertions.assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L)
    }

    @Test
    fun foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        val request = createRequest(999L, 2L, "0000000000003")
        Mockito.`when`<Boolean?>(supplierDsl!!.exists(999L)).thenReturn(false)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: supplier(id=999)")
        Mockito.verifyNoInteractions(storeDsl, bookDsl)
    }

    @Test
    fun foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        val request = createRequest(1L, 999L, "0000000000003")
        Mockito.`when`<Boolean?>(supplierDsl!!.exists(1L)).thenReturn(true)
        Mockito.`when`<Boolean?>(storeDsl!!.exists(999L)).thenReturn(false)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: store(id=999)")
        Mockito.verifyNoInteractions(bookDsl)
    }

    @Test
    fun foreignKeyValidateThrowsWhenBookDoesNotExist() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000999")
        Mockito.`when`<Boolean?>(supplierDsl!!.exists(1L)).thenReturn(true)
        Mockito.`when`<Boolean?>(storeDsl!!.exists(2L)).thenReturn(true)
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("0000000000003")).thenReturn(3L)
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("0000000000999")).thenReturn(null)

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
}
