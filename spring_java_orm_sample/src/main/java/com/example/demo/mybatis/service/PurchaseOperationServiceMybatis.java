package com.example.demo.mybatis.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.mybatis.converter.PurchaseOperationConverterMybatis;
import com.example.demo.mybatis.generator.entity.PurchaseOrderDetailEntity;
import com.example.demo.mybatis.generator.mapper.BookStockMovementMapper;
import com.example.demo.mybatis.generator.mapper.BookStockMapper;
import com.example.demo.mybatis.generator.mapper.PurchaseOrderDetailMapper;
import com.example.demo.mybatis.generator.mapper.PurchaseOrderMapper;
import com.example.demo.mybatis.mapper.BookStockCustomMapper;
import com.example.demo.mybatis.validator.PurchaseDataValidatorMybatis;
import com.example.demo.service.PurchaseOperationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Profile("mybatis")
@RequiredArgsConstructor
public class PurchaseOperationServiceMybatis implements PurchaseOperationService {
    private final PurchaseDataValidatorMybatis dataValidator;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final BookStockCustomMapper bookStockCustomMapper;
    private final BookStockMovementMapper bookStockMovementMapper;
    private final BookStockMapper bookStockMapper;
    private final PurchaseOperationConverterMybatis converter;

    @RetryableOnLockFailure
    @Transactional
    @Override
    public PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) {
        final var bookIdsByIsbn = dataValidator.foreignKeyValidate(request);

        final var now = LocalDateTime.now();
        final var details = converter.toPurchaseInvoiceDetails(request, bookIdsByIsbn, now);
        final var amount = details.stream().mapToLong(PurchaseOrderDetailEntity::getPurchaseInvoiceDetailAmount).sum();
        final var purchaseInvoice = converter.toPurchaseInvoice(request, amount, now);

        purchaseOrderMapper.insert(purchaseInvoice);

        details.forEach(purchaseInvoiceDetail -> {
            purchaseInvoiceDetail.setPurchaseInvoiceId(purchaseInvoice.getId());
            purchaseOrderDetailMapper.insert(purchaseInvoiceDetail);
            bookStockMovementMapper.insert(converter.toBookStockMovement(purchaseInvoice, purchaseInvoiceDetail, now));

            final var bookStock = bookStockCustomMapper.selectByStoreIdAndBookIdWithWriteLock(
                purchaseInvoice.getReceivingStoreId(),
                purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId()
            );

            if (Objects.isNull(bookStock)) {
                final var stock = converter.toBookStock(purchaseInvoice.getReceivingStoreId(), purchaseInvoiceDetail, now);
                bookStockMapper.insert(stock);
                return;
            }

            final var quantity = bookStock.getBookStockQuantity() + purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity();
            bookStock.setBookStockQuantity(quantity);
            bookStock.setUpdateAt(now);
            bookStock.setVersion(bookStock.getVersion() + 1);
            bookStockMapper.updateByPrimaryKey(bookStock);
        });

        return converter.toResponse(purchaseInvoice, details);
    }
}
