package com.example.demo.jooq.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.example.demo.jooq.generated.Tables.BOOK;
import static com.example.demo.jooq.generated.Tables.STORE;
import static com.example.demo.jooq.generated.Tables.SUPPLIER;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class PurchaseDataValidatorJooq {
    private final DSLContext dsl;

    public void foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        if (!dsl.fetchExists(SUPPLIER, SUPPLIER.ID.eq(request.getSupplierId()))) {
            throw new ForeignKeyReferenceNotFoundException("supplier", request.getSupplierId());
        }

        if (!dsl.fetchExists(STORE, STORE.ID.eq(request.getReceivingStoreId()))) {
            throw new ForeignKeyReferenceNotFoundException("store", request.getReceivingStoreId());
        }

        request.getDetails().forEach(detail -> {
            final var bookId = detail.getPurchaseInvoiceDetailBookId();
            if (!dsl.fetchExists(BOOK, BOOK.ID.eq(bookId))) {
                throw new ForeignKeyReferenceNotFoundException("book", bookId);
            }
        });
    }
}
