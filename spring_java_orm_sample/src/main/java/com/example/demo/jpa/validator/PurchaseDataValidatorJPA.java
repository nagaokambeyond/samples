package com.example.demo.jpa.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Store;
import com.example.demo.jpa.entity.Supplier;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.StoreRepository;
import com.example.demo.jpa.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("jpa")
@RequiredArgsConstructor
public class PurchaseDataValidatorJPA {
    private final BookRepository bookRepository;
    private final SupplierRepository supplierRepository;
    private final StoreRepository storeRepository;

    public Map<String, Long> foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        if (!supplierRepository.existsById(request.getSupplierId())) {
            throw new ForeignKeyReferenceNotFoundException(Supplier.class, request.getSupplierId());
        }

        if (!storeRepository.existsById(request.getReceivingStoreId())) {
            throw new ForeignKeyReferenceNotFoundException(Store.class, request.getReceivingStoreId());
        }

        return validateBooks(request.getDetails());
    }

    private Map<String, Long> validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        final var bookIdsByIsbn = new LinkedHashMap<String, Long>();
        details.forEach(detail -> {
            final var isbn = detail.getPurchaseInvoiceDetailIsbn();
            final var book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ForeignKeyReferenceNotFoundException("book", "isbn", isbn));
            bookIdsByIsbn.put(isbn, book.getId());
        });
        return bookIdsByIsbn;
    }
}
