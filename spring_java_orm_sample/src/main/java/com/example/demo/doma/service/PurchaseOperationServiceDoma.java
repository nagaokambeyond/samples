package com.example.demo.doma.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.doma.converter.PurchaseOperationConverterDoma;
import com.example.demo.doma.dao.BookStockCustomDao;
import com.example.demo.doma.generator.dao.BookStockDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDetailDao;
import com.example.demo.doma.generator.entity.BookStock;
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail;
import com.example.demo.doma.validator.PurchaseDataValidatorDoma;
import com.example.demo.service.PurchaseOperationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.OptimisticLockException;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Profile("doma")
@Primary
@RequiredArgsConstructor
public class PurchaseOperationServiceDoma implements PurchaseOperationService {
    private final PurchaseDataValidatorDoma dataValidator;
    private final PurchaseInvoiceDao purchaseInvoiceDao;
    private final PurchaseInvoiceDetailDao purchaseInvoiceDetailDao;
    private final BookStockCustomDao bookStockCustomDao;
    private final BookStockDao bookStockDao;

    private final PurchaseOperationConverterDoma converter;

    @RetryableOnLockFailure
    @Transactional
    @Override
    public PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) {
        dataValidator.foreignKeyValidate(request);

        final var now = LocalDateTime.now();
        final var details = converter.toPurchaseInvoiceDetails(request, now);
        final var amount = details.stream().mapToLong(PurchaseInvoiceDetail::getPurchaseInvoiceDetailAmount).sum();
        final var purchaseInvoice = converter.toPurchaseInvoice(request, amount, now);

        purchaseInvoiceDao.insert(purchaseInvoice);

        details.forEach(purchaseInvoiceDetail -> {
            purchaseInvoiceDetail.setPurchaseInvoiceId(purchaseInvoice.getId());
            purchaseInvoiceDetailDao.insert(purchaseInvoiceDetail);

            final var bookStock = bookStockCustomDao.selectByStoreIdAndBookIdWithWriteLock(
                purchaseInvoice.getReceivingStoreId(),
                purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId()
            );

            if (Objects.isNull(bookStock)) {
                final var stock = converter.toBookStock(purchaseInvoice.getReceivingStoreId(), purchaseInvoiceDetail, now);
                bookStockDao.insert(stock);
                return;
            }

            final var quantity = bookStock.getBookStockQuantity() + purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity();
            bookStock.setBookStockQuantity(quantity);
            bookStock.setUpdateAt(now);
            try {
                bookStockDao.update(bookStock);
            } catch (OptimisticLockException ex) {
                throw new ObjectOptimisticLockingFailureException(BookStock.class, bookStock.getId(), ex);
            }
        });

        return converter.toRespose(purchaseInvoice, details);
    }
}
