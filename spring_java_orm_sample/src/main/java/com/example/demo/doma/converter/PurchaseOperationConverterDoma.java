package com.example.demo.doma.converter;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.generator.entity.BookStock;
import com.example.demo.doma.generator.entity.PurchaseInvoice;
import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseOperationConverterDoma {
    public List<PurchaseInvoiceDetail> toPurchaseInvoiceDetails(
        PurchaseInvoiceCreateRequest request,
        LocalDateTime now
    ) {
        return request.getDetails().stream().map(purchaseInvoiceDetail -> {
            final var row = new PurchaseInvoiceDetail();

            row.setPurchaseInvoiceDetailBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId());
            row.setPurchaseInvoiceDetailUnitPrice(purchaseInvoiceDetail.getPurchaseInvoiceDetailUnitPrice());
            row.setPurchaseInvoiceDetailQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity());

            final var amount = ((long) row.getPurchaseInvoiceDetailUnitPrice() * (long) row.getPurchaseInvoiceDetailQuantity());
            row.setPurchaseInvoiceDetailAmount(amount);
            row.setCreateAt(now);
            row.setUpdateAt(now);

            return row;
        }).toList();
    }

    public PurchaseInvoice toPurchaseInvoice(PurchaseInvoiceCreateRequest request, long amount, LocalDateTime now) {
        final var result = new PurchaseInvoice();
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        result.setPurchaseInvoiceDate(request.getPurchaseInvoiceDate());
        result.setSupplierId(request.getSupplierId());
        result.setReceivingStoreId(request.getReceivingStoreId());
        result.setPurchaseInvoiceAmount(amount);
        result.setCreateAt(now);
        result.setUpdateAt(now);
        return result;
    }

    public BookStock toBookStock(Long storeId, PurchaseInvoiceDetail purchaseInvoiceDetail, LocalDateTime now) {
        final var result = new BookStock();
        result.setBookStockStoreId(storeId);
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId());
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity());
        result.setCreateAt(now);
        result.setUpdateAt(now);

        return result;
    }

    public PurchaseInvoiceResponse toRespose(PurchaseInvoice purchaseInvoice, List<PurchaseInvoiceDetail> details){
        final var list = details.stream().map(row-> new PurchaseInvoiceDetailResponse(
            row.getId(),
            row.getPurchaseInvoiceId(),
            row.getPurchaseInvoiceDetailBookId(),
            row.getPurchaseInvoiceDetailUnitPrice(),
            row.getPurchaseInvoiceDetailQuantity(),
            row.getPurchaseInvoiceDetailAmount(),
            row.getUpdateAt(),
            row.getVersion()
        )).toList();

        return new PurchaseInvoiceResponse(
            purchaseInvoice.getId(),
            purchaseInvoice.getPurchaseInvoiceType(),
            null,
            purchaseInvoice.getPurchaseInvoiceDate(),
            purchaseInvoice.getSupplierId(),
            purchaseInvoice.getReceivingStoreId(),
            purchaseInvoice.getPurchaseInvoiceAmount(),
            purchaseInvoice.getUpdateAt(),
            purchaseInvoice.getVersion(),
            list
        );
    }
}
