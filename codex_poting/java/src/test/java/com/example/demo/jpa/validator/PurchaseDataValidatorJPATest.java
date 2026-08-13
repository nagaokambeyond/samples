package com.example.demo.jpa.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.StoreRepository;
import com.example.demo.jpa.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PurchaseDataValidatorJPATest {
    private BookRepository bookRepository;
    private SupplierRepository supplierRepository;
    private StoreRepository storeRepository;
    private PurchaseDataValidatorJPA validator;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        supplierRepository = mock(SupplierRepository.class);
        storeRepository = mock(StoreRepository.class);
        validator = new PurchaseDataValidatorJPA(bookRepository, supplierRepository, storeRepository);
    }

    @Test
    void foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000004");
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(storeRepository.existsById(2L)).thenReturn(true);
        when(bookRepository.findByIsbn("0000000000003")).thenReturn(Optional.of(book(3L)));
        when(bookRepository.findByIsbn("0000000000004")).thenReturn(Optional.of(book(4L)));

        final var result = validator.foreignKeyValidate(request);

        assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L);
    }

    @Test
    void foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        final var request = createRequest(999L, 2L, "0000000000003");
        when(supplierRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: supplier(id=999)");
        verifyNoInteractions(storeRepository, bookRepository);
    }

    @Test
    void foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        final var request = createRequest(1L, 999L, "0000000000003");
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(storeRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: store(id=999)");
        verifyNoInteractions(bookRepository);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookDoesNotExist() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000999");
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(storeRepository.existsById(2L)).thenReturn(true);
        when(bookRepository.findByIsbn("0000000000003")).thenReturn(Optional.of(book(3L)));
        when(bookRepository.findByIsbn("0000000000999")).thenReturn(Optional.empty());

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

    private Book book(Long id) {
        final var book = new Book();
        book.setId(id);
        return book;
    }
}
