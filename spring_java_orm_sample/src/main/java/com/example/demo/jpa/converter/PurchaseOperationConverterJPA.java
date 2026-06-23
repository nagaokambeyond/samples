package com.example.demo.jpa.converter;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.jpa.entity.BookStock;
import com.example.demo.jpa.entity.PurchaseOrder;
import com.example.demo.jpa.entity.PurchaseOrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseOperationConverterJPA {
    public List<PurchaseOrderDetail> toPurchaseInvoiceDetails(
        PurchaseInvoiceCreateRequest request,
        LocalDateTime now
    ) {
        return request.getDetails().stream().map(purchaseInvoiceDetail -> {
            final var row = new PurchaseOrderDetail();

            row.setPurchaseOrderDetailBookId(purchaseInvoiceDetail.getPurchaseInvoiceDetailBookId());
            row.setPurchaseOrderDetailUnitPrice(purchaseInvoiceDetail.getPurchaseInvoiceDetailUnitPrice());
            row.setPurchaseOrderDetailQuantity(purchaseInvoiceDetail.getPurchaseInvoiceDetailQuantity());

            final var amount = (long) row.getPurchaseOrderDetailUnitPrice() * row.getPurchaseOrderDetailQuantity();
            row.setPurchaseOrderDetailAmount(amount);
            row.setCreateAt(now);
            row.setUpdateAt(now);
            row.setVersion(1L);

            return row;
        }).toList();
    }

    public PurchaseOrder toPurchaseInvoice(PurchaseInvoiceCreateRequest request, long amount, LocalDateTime now) {
        final var result = new PurchaseOrder();
        result.setPurchaseInvoiceType(PurchaseInvoiceType.PURCHASE);
        result.setPurchaseOrderDate(request.getPurchaseInvoiceDate());
        result.setSupplierId(request.getSupplierId());
        result.setReceivingStoreId(request.getReceivingStoreId());
        result.setPurchaseOrderAmount(amount);
        result.setCreateAt(now);
        result.setUpdateAt(now);
        result.setVersion(1L);
        return result;
    }

    public BookStock toBookStock(Long storeId, PurchaseOrderDetail purchaseInvoiceDetail, LocalDateTime now) {
        final var result = new BookStock();
        result.setBookStockStoreId(storeId);
        result.setBookStockBookId(purchaseInvoiceDetail.getPurchaseOrderDetailBookId());
        result.setBookStockQuantity(purchaseInvoiceDetail.getPurchaseOrderDetailQuantity());
        result.setCreateAt(now);
        result.setUpdateAt(now);
        result.setVersion(1L);

        return result;
    }

    public PurchaseInvoiceResponse toResponse(PurchaseOrder purchaseInvoice, List<PurchaseOrderDetail> details) {
        final var list = details.stream().map(row -> new PurchaseInvoiceDetailResponse(
            row.getId(),
            row.getPurchaseOrderId(),
            row.getPurchaseOrderDetailBookId(),
            row.getPurchaseOrderDetailUnitPrice(),
            row.getPurchaseOrderDetailQuantity(),
            row.getPurchaseOrderDetailAmount(),
            row.getUpdateAt(),
            row.getVersion()
        )).toList();

        return new PurchaseInvoiceResponse(
            purchaseInvoice.getId(),
            purchaseInvoice.getPurchaseInvoiceType(),
            purchaseInvoice.getReturnPurchaseOrderId(),
            purchaseInvoice.getPurchaseOrderDate(),
            purchaseInvoice.getSupplierId(),
            purchaseInvoice.getReceivingStoreId(),
            purchaseInvoice.getPurchaseOrderAmount(),
            purchaseInvoice.getUpdateAt(),
            purchaseInvoice.getVersion(),
            list
        );
    }
}
