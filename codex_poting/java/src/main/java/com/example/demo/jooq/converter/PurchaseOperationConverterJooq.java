package com.example.demo.jooq.converter;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceDetailResponse;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow;
import com.example.demo.jooq.entity.PurchaseInvoiceRow;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("jooq")
public class PurchaseOperationConverterJooq {
    public PurchaseInvoiceResponse toResponse(PurchaseInvoiceRow purchaseInvoice, List<PurchaseInvoiceDetailRow> details) {
        final var response = new PurchaseInvoiceResponse();
        response.setId(purchaseInvoice.getId());
        response.setPurchaseInvoiceType(PurchaseInvoiceType.of(purchaseInvoice.getPurchaseInvoiceType()));
        response.setReturnPurchaseInvoiceId(purchaseInvoice.getReturnPurchaseInvoiceId());
        response.setPurchaseInvoiceDate(purchaseInvoice.getPurchaseInvoiceDate());
        response.setSupplierId(purchaseInvoice.getSupplierId());
        response.setReceivingStoreId(purchaseInvoice.getReceivingStoreId());
        response.setPurchaseInvoiceAmount(purchaseInvoice.getPurchaseInvoiceAmount());
        response.setUpdateAt(purchaseInvoice.getUpdateAt());
        response.setVersion(purchaseInvoice.getVersion());
        response.setDetail(details.stream().map(this::toResponse).toList());
        return response;
    }

    public long calculateAmount(PurchaseInvoiceCreateRequest request) {
        return request.getDetails().stream()
            .mapToLong(this::calculateAmount)
            .sum();
    }

    public long calculateAmount(PurchaseInvoiceDetailCreateRequest detail) {
        return (long) detail.getPurchaseInvoiceDetailUnitPrice() * (long) detail.getPurchaseInvoiceDetailQuantity();
    }

    private PurchaseInvoiceDetailResponse toResponse(PurchaseInvoiceDetailRow row) {
        final var response = new PurchaseInvoiceDetailResponse();
        response.setId(row.getId());
        response.setPurchaseInvoiceId(row.getPurchaseInvoiceId());
        response.setPurchaseInvoiceDetailBookId(row.getPurchaseInvoiceDetailBookId());
        response.setPurchaseInvoiceDetailUnitPrice(row.getPurchaseInvoiceDetailUnitPrice());
        response.setPurchaseInvoiceDetailQuantity(row.getPurchaseInvoiceDetailQuantity());
        response.setPurchaseInvoiceDetailAmount(row.getPurchaseInvoiceDetailAmount());
        response.setVersion(row.getVersion());
        return response;
    }
}
