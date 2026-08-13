package com.example.demo.jooq.entity;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class PurchaseInvoiceRow {
    Long id;
    Integer purchaseInvoiceType;
    Long returnPurchaseInvoiceId;
    LocalDate purchaseInvoiceDate;
    Long supplierId;
    Long receivingStoreId;
    Long purchaseInvoiceAmount;
    LocalDateTime updateAt;
    Long version;
}
