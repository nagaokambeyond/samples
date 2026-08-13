package com.example.demo.jooq.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jooq.dsl.BookDsl;
import com.example.demo.jooq.dsl.StoreDsl;
import com.example.demo.jooq.dsl.SupplierDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PurchaseDataValidatorJooqTest {
    private BookDsl bookDsl;
    private StoreDsl storeDsl;
    private SupplierDsl supplierDsl;
    private PurchaseDataValidatorJooq validator;

    @BeforeEach
    void setUp() {
        bookDsl = mock(BookDsl.class);
        storeDsl = mock(StoreDsl.class);
        supplierDsl = mock(SupplierDsl.class);
        validator = new PurchaseDataValidatorJooq(bookDsl, storeDsl, supplierDsl);
    }

    @Test
    void foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000004");
        when(supplierDsl.exists(1L)).thenReturn(true);
        when(storeDsl.exists(2L)).thenReturn(true);
        when(bookDsl.selectIdByIsbn("0000000000003")).thenReturn(3L);
        when(bookDsl.selectIdByIsbn("0000000000004")).thenReturn(4L);

        final var result = validator.foreignKeyValidate(request);

        assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L);
    }

    @Test
    void foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        final var request = createRequest(999L, 2L, "0000000000003");
        when(supplierDsl.exists(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: supplier(id=999)");
        verifyNoInteractions(storeDsl, bookDsl);
    }

    @Test
    void foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        final var request = createRequest(1L, 999L, "0000000000003");
        when(supplierDsl.exists(1L)).thenReturn(true);
        when(storeDsl.exists(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: store(id=999)");
        verifyNoInteractions(bookDsl);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookDoesNotExist() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000999");
        when(supplierDsl.exists(1L)).thenReturn(true);
        when(storeDsl.exists(2L)).thenReturn(true);
        when(bookDsl.selectIdByIsbn("0000000000003")).thenReturn(3L);
        when(bookDsl.selectIdByIsbn("0000000000999")).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book(isbn=0000000000999)");
    }

    private PurchaseInvoiceCreateRequest createRequest(Long supplierId, Long receivingStoreId, String... isbns) {
        final var details = List.of(isbns).stream()
            .map(isbn -> new PurchaseInvoiceDetailCreateRequest(isbn, 1000, 1))
            .toList();
        return new PurchaseInvoiceCreateRequest(LocalDate.of(2026, 2, 1), supplierId, receivingStoreId, details);
    }
}
