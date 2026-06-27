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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Profile("doma")
@RequiredArgsConstructor
public class PurchaseDataValidatorDoma {
    private final BookDao bookDao;
    private final PurchaseInvoiceDao purchaseInvoiceDao;
    private final SupplierDao supplierDao;
    private final StoreDao storeDao;

    public void foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        final var supplier = supplierDao.selectById(request.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new ForeignKeyReferenceNotFoundException(Supplier.class, request.getSupplierId());
        }

        final var store = storeDao.selectById(request.getReceivingStoreId());
        if (Objects.isNull(store)) {
            throw new ForeignKeyReferenceNotFoundException(Store.class, request.getReceivingStoreId());
        }

        validateBooks(request.getDetails());
    }

    public void returnPurchaseInvoiceIdValidate(Long returnPurchaseInvoiceId) {
        if (Objects.isNull(returnPurchaseInvoiceId)) {
            return;
        }

        final var purchaseInvoice = purchaseInvoiceDao.selectById(returnPurchaseInvoiceId);
        if (Objects.isNull(purchaseInvoice)) {
            throw new ForeignKeyReferenceNotFoundException(PurchaseInvoice.class, returnPurchaseInvoiceId);
        }

        if (purchaseInvoice.getPurchaseInvoiceType() != PurchaseInvoiceType.PURCHASE) {
            throw new ForeignKeyReferenceNotFoundException(PurchaseInvoice.class, returnPurchaseInvoiceId);
        }
    }

    private void validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        details.forEach(detail -> {
            final var bookId = detail.getPurchaseInvoiceDetailBookId();
            if (bookDao.selectById(bookId) == null) {
                throw new ForeignKeyReferenceNotFoundException(Book.class, bookId);
            }
        });
    }
}
