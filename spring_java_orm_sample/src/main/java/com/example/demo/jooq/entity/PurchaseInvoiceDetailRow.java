package com.example.demo.jooq.entity;

import lombok.Value;

@Value
public class PurchaseInvoiceDetailRow {
    Long id;
    Long purchaseInvoiceId;
    Long purchaseInvoiceDetailBookId;
    Integer purchaseInvoiceDetailUnitPrice;
    Integer purchaseInvoiceDetailQuantity;
    Long purchaseInvoiceDetailAmount;
    Long version;
}
