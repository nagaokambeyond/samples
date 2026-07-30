package com.example.demo.doma.validator

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.doma.dao.BookCustomDao
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao
import com.example.demo.doma.generator.dao.StoreDao
import com.example.demo.doma.generator.dao.SupplierDao
import com.example.demo.doma.generator.entity.Book
import com.example.demo.doma.generator.entity.PurchaseInvoice
import com.example.demo.doma.generator.entity.Store
import com.example.demo.doma.generator.entity.Supplier
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.util.List

internal class PurchaseDataValidatorDomaTest {
    private var bookCustomDao: BookCustomDao? = null
    private var purchaseInvoiceDao: PurchaseInvoiceDao? = null
    private var supplierDao: SupplierDao? = null
    private var storeDao: StoreDao? = null
    private var validator: PurchaseDataValidatorDoma? = null

    @BeforeEach
    fun setUp() {
        bookCustomDao = Mockito.mock<BookCustomDao>(BookCustomDao::class.java)
        purchaseInvoiceDao = Mockito.mock<PurchaseInvoiceDao>(PurchaseInvoiceDao::class.java)
        supplierDao = Mockito.mock<SupplierDao>(SupplierDao::class.java)
        storeDao = Mockito.mock<StoreDao>(StoreDao::class.java)
        validator = PurchaseDataValidatorDoma(bookCustomDao!!, purchaseInvoiceDao!!, supplierDao!!, storeDao!!)
    }

    @Test
    fun foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000004")
        Mockito.`when`<Supplier?>(supplierDao!!.selectById(1L)).thenReturn(Supplier())
        Mockito.`when`<Store?>(storeDao!!.selectById(2L)).thenReturn(Store())
        Mockito.`when`<Book?>(bookCustomDao!!.selectByIsbn("0000000000003")).thenReturn(book(3L))
        Mockito.`when`<Book?>(bookCustomDao!!.selectByIsbn("0000000000004")).thenReturn(book(4L))

        val result = validator!!.foreignKeyValidate(request)

        Assertions.assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L)
    }

    @Test
    fun foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        val request = createRequest(999L, 2L, "0000000000003")
        Mockito.`when`<Supplier?>(supplierDao!!.selectById(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: supplier(id=999)")
    }

    @Test
    fun foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        val request = createRequest(1L, 999L, "0000000000003")
        Mockito.`when`<Supplier?>(supplierDao!!.selectById(1L)).thenReturn(Supplier())
        Mockito.`when`<Store?>(storeDao!!.selectById(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: store(id=999)")
    }

    @Test
    fun foreignKeyValidateThrowsWhenBookDoesNotExist() {
        val request = createRequest(1L, 2L, "0000000000003", "0000000000999")
        Mockito.`when`<Supplier?>(supplierDao!!.selectById(1L)).thenReturn(Supplier())
        Mockito.`when`<Store?>(storeDao!!.selectById(2L)).thenReturn(Store())
        Mockito.`when`<Book?>(bookCustomDao!!.selectByIsbn("0000000000003")).thenReturn(book(3L))
        Mockito.`when`<Book?>(bookCustomDao!!.selectByIsbn("0000000000999")).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book(isbn=0000000000999)")
    }

    @Test
    fun returnPurchaseInvoiceIdValidateAllowsNull() {
        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.returnPurchaseInvoiceIdValidate(null) })
        Mockito.verifyNoInteractions(purchaseInvoiceDao)
    }

    @Test
    fun returnPurchaseInvoiceIdValidateAllowsPurchaseInvoice() {
        val purchaseInvoice = PurchaseInvoice()
        purchaseInvoice.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE)
        Mockito.`when`<PurchaseInvoice?>(purchaseInvoiceDao!!.selectById(1L)).thenReturn(purchaseInvoice)

        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.returnPurchaseInvoiceIdValidate(1L) })
    }

    @Test
    fun returnPurchaseInvoiceIdValidateThrowsWhenPurchaseInvoiceDoesNotExist() {
        Mockito.`when`<PurchaseInvoice?>(purchaseInvoiceDao!!.selectById(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.returnPurchaseInvoiceIdValidate(999L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: purchase_invoice(id=999)")
    }

    @Test
    fun returnPurchaseInvoiceIdValidateThrowsWhenInvoiceTypeIsNotPurchase() {
        val purchaseInvoice = PurchaseInvoice()
        purchaseInvoice.setPurchaseInvoiceType(PurchaseInvoiceType.RETURN_PURCHASE)
        Mockito.`when`<PurchaseInvoice?>(purchaseInvoiceDao!!.selectById(2L)).thenReturn(purchaseInvoice)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.returnPurchaseInvoiceIdValidate(2L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: purchase_invoice(id=2)")
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

    private fun book(id: Long?): Book {
        val book = Book()
        book.setId(id)
        return book
    }
}
