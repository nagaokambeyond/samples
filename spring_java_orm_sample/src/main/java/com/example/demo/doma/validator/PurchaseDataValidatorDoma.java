package com.example.demo.doma.validator;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.dao.BookCustomDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao;
import com.example.demo.doma.generator.dao.StoreDao;
import com.example.demo.doma.generator.dao.SupplierDao;
import com.example.demo.doma.generator.entity.PurchaseInvoice;
import com.example.demo.doma.generator.entity.Store;
import com.example.demo.doma.generator.entity.Supplier;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Profile("doma")
@RequiredArgsConstructor
public class PurchaseDataValidatorDoma {
    private final BookCustomDao bookCustomDao;
    private final PurchaseInvoiceDao purchaseInvoiceDao;
    private final SupplierDao supplierDao;
    private final StoreDao storeDao;

    public Map<String, Long> foreignKeyValidate(PurchaseInvoiceCreateRequest request) {
        final var supplier = supplierDao.selectById(request.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new ForeignKeyReferenceNotFoundException(Supplier.class, request.getSupplierId());
        }

        final var store = storeDao.selectById(request.getReceivingStoreId());
        if (Objects.isNull(store)) {
            throw new ForeignKeyReferenceNotFoundException(Store.class, request.getReceivingStoreId());
        }

        return validateBooks(request.getDetails());
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

    private Map<String, Long> validateBooks(List<PurchaseInvoiceDetailCreateRequest> details) {
        final var bookIdsByIsbn = new LinkedHashMap<String, Long>();
        details.forEach(detail -> {
            final var isbn = detail.getPurchaseInvoiceDetailIsbn();
            final var book = bookCustomDao.selectByIsbn(isbn);
            if (Objects.isNull(book)) {
                throw new ForeignKeyReferenceNotFoundException("book", "isbn", isbn);
            }
            bookIdsByIsbn.put(isbn, book.getId());
        });
        return bookIdsByIsbn;
    }
}
