package com.example.demo.api.controller;

import com.example.demo.api.PurchaseOperationApi;
import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import com.example.demo.service.PurchaseOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class PurchaseOperationApiController implements PurchaseOperationApi {
    private final PurchaseOperationService service;

    @Override
    public PurchaseInvoiceResponse createPurchaseInvoice(PurchaseInvoiceCreateRequest request) {
        return service.create(request);
    }
}
