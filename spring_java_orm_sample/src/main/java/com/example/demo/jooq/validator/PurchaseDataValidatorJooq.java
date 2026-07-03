package com.example.demo.jooq.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jooq.dsl.BookDsl;
import com.example.demo.jooq.dsl.StoreDsl;
import com.example.demo.jooq.dsl.SupplierDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class PurchaseDataValidatorJooq {
    private final BookDsl bookDsl;
    private final StoreDsl storeDsl;
    private final SupplierDsl supplierDsl;

    public Map<String, Long> foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        if (!supplierDsl.exists(request.getSupplierId())) {
            throw new ForeignKeyReferenceNotFoundException("supplier", request.getSupplierId());
        }

        if (!storeDsl.exists(request.getReceivingStoreId())) {
            throw new ForeignKeyReferenceNotFoundException("store", request.getReceivingStoreId());
        }

        final var bookIdsByIsbn = new LinkedHashMap<String, Long>();
        request.getDetails().forEach(detail -> {
            final var isbn = detail.getPurchaseInvoiceDetailIsbn();
            final var bookId = bookDsl.selectIdByIsbn(isbn);
            if (Objects.isNull(bookId)) {
                throw new ForeignKeyReferenceNotFoundException("book", "isbn", isbn);
            }
            bookIdsByIsbn.put(isbn, bookId);
        });
        return bookIdsByIsbn;
    }
}
