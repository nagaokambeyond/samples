package com.example.demo.jooq.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.jooq.converter.PurchaseOperationConverterJooq;
import com.example.demo.jooq.dsl.PurchaseOperationDsl;
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow;
import com.example.demo.jooq.validator.PurchaseDataValidatorJooq;
import com.example.demo.service.PurchaseOperationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Profile("jooq")
@RequiredArgsConstructor
public class PurchaseOperationServiceJooq implements PurchaseOperationService {
    private final PurchaseOperationDsl purchaseOperationDsl;
    private final PurchaseDataValidatorJooq dataValidator;
    private final PurchaseOperationConverterJooq converter;

    @RetryableOnLockFailure
    @Transactional
    @Override
    public PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) {
        final var bookIdsByIsbn = dataValidator.foreignKeyValidate(request);

        final var now = LocalDateTime.now();
        final var amount = converter.calculateAmount(request);
        final var purchaseInvoice = purchaseOperationDsl.insertPurchaseInvoice(request, amount, now);
        final var details = new ArrayList<PurchaseInvoiceDetailRow>();

        request.getDetails().forEach(detailRequest -> {
            final var detailAmount = converter.calculateAmount(detailRequest);
            final var bookId = bookIdsByIsbn.get(detailRequest.getPurchaseInvoiceDetailIsbn());
            final var detail = purchaseOperationDsl.insertPurchaseInvoiceDetail(
                purchaseInvoice.getId(),
                detailRequest,
                bookId,
                detailAmount,
                now
            );
            details.add(detail);
            purchaseOperationDsl.insertBookStockMovement(purchaseInvoice.getReceivingStoreId(), purchaseInvoice, detail, now);
            purchaseOperationDsl.addStockQuantity(purchaseInvoice.getReceivingStoreId(), detail, now);
        });

        return converter.toResponse(purchaseInvoice, details);
    }
}
