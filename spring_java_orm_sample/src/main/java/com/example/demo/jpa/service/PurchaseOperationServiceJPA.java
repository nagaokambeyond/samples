package com.example.demo.jpa.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.jpa.converter.PurchaseOperationConverterJPA;
import com.example.demo.jpa.entity.PurchaseOrderDetail;
import com.example.demo.jpa.repository.BookStockRepository;
import com.example.demo.jpa.repository.PurchaseOrderDetailRepository;
import com.example.demo.jpa.repository.PurchaseOrderRepository;
import com.example.demo.jpa.validator.PurchaseDataValidatorJPA;
import com.example.demo.service.PurchaseOperationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Profile("jpa")
@RequiredArgsConstructor
public class PurchaseOperationServiceJPA implements PurchaseOperationService {
    private final PurchaseDataValidatorJPA dataValidator;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final BookStockRepository bookStockRepository;
    private final PurchaseOperationConverterJPA converter;

    @RetryableOnLockFailure
    @Transactional
    @Override
    public PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) {
        dataValidator.foreignKeyValidate(request);

        final var now = LocalDateTime.now();
        final var details = converter.toPurchaseInvoiceDetails(request, now);
        final var amount = details.stream().mapToLong(PurchaseOrderDetail::getPurchaseOrderDetailAmount).sum();
        final var purchaseInvoice = purchaseOrderRepository.save(converter.toPurchaseInvoice(request, amount, now));
        final var savedDetails = details.stream().map(purchaseInvoiceDetail -> {
            purchaseInvoiceDetail.setPurchaseOrderId(purchaseInvoice.getId());
            final var savedDetail = purchaseOrderDetailRepository.save(purchaseInvoiceDetail);

            final var bookStock = bookStockRepository.findByStoreIdAndBookIdWithWriteLock(
                purchaseInvoice.getReceivingStoreId(),
                savedDetail.getPurchaseOrderDetailBookId()
            );

            bookStock.ifPresentOrElse(stock -> {
                final var quantity = stock.getBookStockQuantity() + savedDetail.getPurchaseOrderDetailQuantity();
                stock.setBookStockQuantity(quantity);
                stock.setUpdateAt(now);
                bookStockRepository.save(stock);
            }, () -> {
                final var stock = converter.toBookStock(purchaseInvoice.getReceivingStoreId(), savedDetail, now);
                bookStockRepository.save(stock);
            });
            return savedDetail;
        }).toList();

        return converter.toResponse(purchaseInvoice, savedDetails);
    }
}
