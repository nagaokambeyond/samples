package com.example.demo.mybatis.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import com.example.demo.mybatis.generator.entity.StoreEntity;
import com.example.demo.mybatis.generator.entity.SupplierEntity;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.generator.mapper.StoreMapper;
import com.example.demo.mybatis.generator.mapper.SupplierMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PurchaseDataValidatorMybatisTest {
    private BookMapper bookMapper;
    private SupplierMapper supplierMapper;
    private StoreMapper storeMapper;
    private PurchaseDataValidatorMybatis validator;

    @BeforeEach
    void setUp() {
        bookMapper = mock(BookMapper.class);
        supplierMapper = mock(SupplierMapper.class);
        storeMapper = mock(StoreMapper.class);
        validator = new PurchaseDataValidatorMybatis(bookMapper, supplierMapper, storeMapper);
    }

    @Test
    void foreignKeyValidateAllowsExistingSupplierStoreAndBooks() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000004");
        when(supplierMapper.selectByPrimaryKey(1L)).thenReturn(new SupplierEntity());
        when(storeMapper.selectByPrimaryKey(2L)).thenReturn(new StoreEntity());
        when(bookMapper.selectByExample(any(BookEntityExample.class)))
            .thenReturn(List.of(book(3L)))
            .thenReturn(List.of(book(4L)));

        final var result = validator.foreignKeyValidate(request);

        assertThat(result)
            .containsEntry("0000000000003", 3L)
            .containsEntry("0000000000004", 4L);
    }

    @Test
    void foreignKeyValidateThrowsWhenSupplierDoesNotExist() {
        final var request = createRequest(999L, 2L, "0000000000003");
        when(supplierMapper.selectByPrimaryKey(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: supplier(id=999)");
        verifyNoInteractions(storeMapper, bookMapper);
    }

    @Test
    void foreignKeyValidateThrowsWhenStoreDoesNotExist() {
        final var request = createRequest(1L, 999L, "0000000000003");
        when(supplierMapper.selectByPrimaryKey(1L)).thenReturn(new SupplierEntity());
        when(storeMapper.selectByPrimaryKey(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: store(id=999)");
        verifyNoInteractions(bookMapper);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookDoesNotExist() {
        final var request = createRequest(1L, 2L, "0000000000003", "0000000000999");
        when(supplierMapper.selectByPrimaryKey(1L)).thenReturn(new SupplierEntity());
        when(storeMapper.selectByPrimaryKey(2L)).thenReturn(new StoreEntity());
        when(bookMapper.selectByExample(any(BookEntityExample.class)))
            .thenReturn(List.of(book(3L)))
            .thenReturn(List.of());

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

    private BookEntity book(Long id) {
        final var book = new BookEntity();
        book.setId(id);
        return book;
    }
}
