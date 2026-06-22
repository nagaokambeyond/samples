package com.example.demo.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import lombok.NonNull;

public interface PurchaseOperationService {
    PurchaseInvoiceResponse create(@NonNull PurchaseInvoiceCreateRequest request) ;
}
