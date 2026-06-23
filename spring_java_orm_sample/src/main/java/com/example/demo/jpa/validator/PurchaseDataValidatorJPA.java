package com.example.demo.jpa.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.entity.Store;
import com.example.demo.jpa.entity.Supplier;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.StoreRepository;
import com.example.demo.jpa.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseDataValidatorJPA {
    private final BookRepository bookRepository;
    private final SupplierRepository supplierRepository;
    private final StoreRepository storeRepository;

    public void foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        if (!supplierRepository.existsById(request.getSupplierId())) {
            throw new ForeignKeyReferenceNotFoundException(Supplier.class, request.getSupplierId());
        }

        if (!storeRepository.existsById(request.getReceivingStoreId())) {
            throw new ForeignKeyReferenceNotFoundException(Store.class, request.getReceivingStoreId());
        }

        validateBooks(request.getDetails());
    }

    private void validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        details.forEach(detail -> {
            final var bookId = detail.getPurchaseInvoiceDetailBookId();
            if (!bookRepository.existsById(bookId)) {
                throw new ForeignKeyReferenceNotFoundException(Book.class, bookId);
            }
        });
    }
}
