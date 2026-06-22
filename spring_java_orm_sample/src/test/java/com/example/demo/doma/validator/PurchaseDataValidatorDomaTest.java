package com.example.demo.doma.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.generator.dao.BookDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao;
import com.example.demo.doma.generator.dao.StoreDao;
import com.example.demo.doma.generator.dao.SupplierDao;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.doma.generator.entity.PurchaseInvoice;
import com.example.demo.doma.generator.entity.Store;
import com.example.demo.doma.generator.entity.Supplier;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PurchaseDataValidatorDomaTest {
    private BookDao bookDao;
    private PurchaseInvoiceDao purchaseInvoiceDao;
    private SupplierDao supplierDao;
    private StoreDao storeDao;
    private PurchaseDataValidatorDoma validator;

    @BeforeEach
    void setUp() {
        bookDao = mock(BookDao.class);
        purchaseInvoiceDao = mock(PurchaseInvoiceDao.class);
        supplierDao = mock(SupplierDao.class);
        storeDao = mock(StoreDao.class);
        validator = new PurchaseDataValidatorDoma(bookDao, purchaseInvoiceDao, supplierDao, storeDao);
    }

    @Test
    void foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        final var request = createRequest(1L, 2L, 3L, 4L);
        when(supplierDao.selectById(1L)).thenReturn(new Supplier());
        when(storeDao.selectById(2L)).thenReturn(new Store());
        when(bookDao.selectById(3L)).thenReturn(new Book());
        when(bookDao.selectById(4L)).thenReturn(new Book());

        assertThatNoException().isThrownBy(() -> validator.foreignKeyValidate(request));
    }

    @Test
    void foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        final var request = createRequest(999L, 2L, 3L);
        when(supplierDao.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: supplier(id=999)");
    }

    @Test
    void foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        final var request = createRequest(1L, 999L, 3L);
        when(supplierDao.selectById(1L)).thenReturn(new Supplier());
        when(storeDao.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: store(id=999)");
    }

    @Test
    void foreignKeyValidateThrowsWhenBookDoesNotExist() {
        final var request = createRequest(1L, 2L, 3L, 999L);
        when(supplierDao.selectById(1L)).thenReturn(new Supplier());
        when(storeDao.selectById(2L)).thenReturn(new Store());
        when(bookDao.selectById(3L)).thenReturn(new Book());
        when(bookDao.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book(id=999)");
    }

    @Test
    void returnPurchaseInvoiceIdValidateAllowsNull() {
        assertThatNoException().isThrownBy(() -> validator.returnPurchaseInvoiceIdValidate(null));
        verifyNoInteractions(purchaseInvoiceDao);
    }

    @Test
    void returnPurchaseInvoiceIdValidateAllowsPurchaseInvoice() {
        final var purchaseInvoice = new PurchaseInvoice();
        purchaseInvoice.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        when(purchaseInvoiceDao.selectById(1L)).thenReturn(purchaseInvoice);

        assertThatNoException().isThrownBy(() -> validator.returnPurchaseInvoiceIdValidate(1L));
    }

    @Test
    void returnPurchaseInvoiceIdValidateThrowsWhenPurchaseInvoiceDoesNotExist() {
        when(purchaseInvoiceDao.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.returnPurchaseInvoiceIdValidate(999L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: purchase_invoice(id=999)");
    }

    @Test
    void returnPurchaseInvoiceIdValidateThrowsWhenInvoiceTypeIsNotPurchase() {
        final var purchaseInvoice = new PurchaseInvoice();
        purchaseInvoice.setPurchaseInvoiceType(PurchaseInvoiceType.RETURN_PURCHASE);
        when(purchaseInvoiceDao.selectById(2L)).thenReturn(purchaseInvoice);

        assertThatThrownBy(() -> validator.returnPurchaseInvoiceIdValidate(2L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: purchase_invoice(id=2)");
    }

    private PurchaseInvoiceCreateRequest createRequest(Long supplierId, Long receivingStoreId, Long... bookIds) {
        final var details = List.of(bookIds).stream()
            .map(bookId -> new PurchaseInvoiceDetailCreateRequest(bookId, 1000, 1))
            .toList();
        return new PurchaseInvoiceCreateRequest(LocalDate.of(2026, 2, 1), supplierId, receivingStoreId, details);
    }
}
