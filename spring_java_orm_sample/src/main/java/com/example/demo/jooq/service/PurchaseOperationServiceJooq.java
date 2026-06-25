package com.example.demo.jooq.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.jooq.converter.PurchaseOperationConverterJooq;
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow;
import com.example.demo.jooq.entity.PurchaseInvoiceRow;
import com.example.demo.jooq.validator.PurchaseDataValidatorJooq;
import com.example.demo.service.PurchaseOperationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

import static com.example.demo.jooq.generated.Tables.BOOK_STOCK;
import static com.example.demo.jooq.generated.Tables.PURCHASE_INVOICE;
import static com.example.demo.jooq.generated.Tables.PURCHASE_INVOICE_DETAIL;

@Service
@RequiredArgsConstructor
public class PurchaseOperationServiceJooq implements PurchaseOperationService {
    private final DSLContext dsl;
    private final PurchaseDataValidatorJooq dataValidator;
    private final PurchaseOperationConverterJooq converter;

    @RetryableOnLockFailure
    @Transactional
    @Override
    public PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) {
        dataValidator.foreignKeyValidate(request);

        final var now = LocalDateTime.now();
        final var amount = converter.calculateAmount(request);
        final var purchaseInvoice = insertPurchaseInvoice(request, amount, now);
        final var details = new ArrayList<PurchaseInvoiceDetailRow>();

        request.getDetails().forEach(detailRequest -> {
            final var detail = insertPurchaseInvoiceDetail(purchaseInvoice.getId(), detailRequest, now);
            details.add(detail);
            addStockQuantity(purchaseInvoice.getReceivingStoreId(), detail, now);
        });

        return converter.toResponse(purchaseInvoice, details);
    }

    private PurchaseInvoiceRow insertPurchaseInvoice(PurchaseInvoiceCreateRequest request, long amount, LocalDateTime now) {
        final var id = dsl.insertInto(PURCHASE_INVOICE)
            .set(PURCHASE_INVOICE.PURCHASE_INVOICE_TYPE, PurchaseInvoiceType.PURCHASE.getValue())
            .set(PURCHASE_INVOICE.RETURN_PURCHASE_INVOICE_ID, (Long) null)
            .set(PURCHASE_INVOICE.PURCHASE_INVOICE_DATE, request.getPurchaseInvoiceDate())
            .set(PURCHASE_INVOICE.SUPPLIER_ID, request.getSupplierId())
            .set(PURCHASE_INVOICE.RECEIVING_STORE_ID, request.getReceivingStoreId())
            .set(PURCHASE_INVOICE.PURCHASE_INVOICE_AMOUNT, amount)
            .set(PURCHASE_INVOICE.CREATE_AT, now)
            .set(PURCHASE_INVOICE.UPDATE_AT, now)
            .set(PURCHASE_INVOICE.VERSION, 1L)
            .returningResult(PURCHASE_INVOICE.ID)
            .fetchOne(PURCHASE_INVOICE.ID);
        final var purchaseInvoiceId = Objects.requireNonNull(id);

        return new PurchaseInvoiceRow(
            purchaseInvoiceId,
            PurchaseInvoiceType.PURCHASE.getValue(),
            null,
            request.getPurchaseInvoiceDate(),
            request.getSupplierId(),
            request.getReceivingStoreId(),
            amount,
            now,
            1L
        );
    }

    private PurchaseInvoiceDetailRow insertPurchaseInvoiceDetail(
        Long purchaseInvoiceId,
        PurchaseInvoiceDetailCreateRequest request,
        LocalDateTime now
    ) {
        final var detailAmount = converter.calculateAmount(request);
        final var id = dsl.insertInto(PURCHASE_INVOICE_DETAIL)
            .set(PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_ID, purchaseInvoiceId)
            .set(PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_BOOK_ID, request.getPurchaseInvoiceDetailBookId())
            .set(PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_UNIT_PRICE, request.getPurchaseInvoiceDetailUnitPrice())
            .set(PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_QUANTITY, request.getPurchaseInvoiceDetailQuantity())
            .set(PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_AMOUNT, detailAmount)
            .set(PURCHASE_INVOICE_DETAIL.CREATE_AT, now)
            .set(PURCHASE_INVOICE_DETAIL.UPDATE_AT, now)
            .set(PURCHASE_INVOICE_DETAIL.VERSION, 1L)
            .returningResult(PURCHASE_INVOICE_DETAIL.ID)
            .fetchOne(PURCHASE_INVOICE_DETAIL.ID);
        final var detailId = Objects.requireNonNull(id);

        return new PurchaseInvoiceDetailRow(
            detailId,
            purchaseInvoiceId,
            request.getPurchaseInvoiceDetailBookId(),
            request.getPurchaseInvoiceDetailUnitPrice(),
            request.getPurchaseInvoiceDetailQuantity(),
            detailAmount,
            1L
        );
    }

    private void addStockQuantity(Long storeId, PurchaseInvoiceDetailRow detail, LocalDateTime now) {
        final var bookStock = selectBookStockForUpdate(storeId, detail.getPurchaseInvoiceDetailBookId());
        if (Objects.isNull(bookStock)) {
            dsl.insertInto(BOOK_STOCK)
                .set(BOOK_STOCK.BOOK_STOCK_STORE_ID, storeId)
                .set(BOOK_STOCK.BOOK_STOCK_BOOK_ID, detail.getPurchaseInvoiceDetailBookId())
                .set(BOOK_STOCK.BOOK_STOCK_QUANTITY, detail.getPurchaseInvoiceDetailQuantity())
                .set(BOOK_STOCK.CREATE_AT, now)
                .set(BOOK_STOCK.UPDATE_AT, now)
                .set(BOOK_STOCK.VERSION, 1L)
                .execute();
            return;
        }

        final var quantity = bookStock.get(BOOK_STOCK.BOOK_STOCK_QUANTITY) + detail.getPurchaseInvoiceDetailQuantity();
        final var version = bookStock.get(BOOK_STOCK.VERSION);
        dsl.update(BOOK_STOCK)
            .set(BOOK_STOCK.BOOK_STOCK_QUANTITY, quantity)
            .set(BOOK_STOCK.UPDATE_AT, now)
            .set(BOOK_STOCK.VERSION, version + 1)
            .where(BOOK_STOCK.ID.eq(bookStock.get(BOOK_STOCK.ID)))
            .execute();
    }

    private org.jooq.Record3<Long, Integer, Long> selectBookStockForUpdate(Long storeId, Long bookId) {
        return executeWithLockException(() -> dsl.select(
                BOOK_STOCK.ID,
                BOOK_STOCK.BOOK_STOCK_QUANTITY,
                BOOK_STOCK.VERSION
            )
            .from(BOOK_STOCK)
            .where(BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(storeId))
            .and(BOOK_STOCK.BOOK_STOCK_BOOK_ID.eq(bookId))
            .forUpdate()
            .noWait()
            .fetchOne());
    }

    private <T> T executeWithLockException(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            throw new PessimisticLockingFailureException("jOOQ write lock could not be acquired", ex);
        }
    }
}
