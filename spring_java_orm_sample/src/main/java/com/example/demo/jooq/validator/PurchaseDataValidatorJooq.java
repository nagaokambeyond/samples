package com.example.demo.jooq.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jooq.dsl.BookDsl;
import com.example.demo.jooq.dsl.StoreDsl;
import com.example.demo.jooq.dsl.SupplierDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class PurchaseDataValidatorJooq {
    private final BookDsl bookDsl;
    private final StoreDsl storeDsl;
    private final SupplierDsl supplierDsl;

    public void foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        if (!supplierDsl.exists(request.getSupplierId())) {
            throw new ForeignKeyReferenceNotFoundException("supplier", request.getSupplierId());
        }

        if (!storeDsl.exists(request.getReceivingStoreId())) {
            throw new ForeignKeyReferenceNotFoundException("store", request.getReceivingStoreId());
        }

        request.getDetails().forEach(detail -> {
            final var bookId = detail.getPurchaseInvoiceDetailBookId();
            if (!bookDsl.exists(bookId)) {
                throw new ForeignKeyReferenceNotFoundException("book", bookId);
            }
        });
    }
}
