package com.example.demo.api;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.response.PurchaseInvoiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/purchases")
@Tag(name = "Purchase", description = "仕入API")
public interface PurchaseOperationApi {
    @PostMapping("/create")
    @Operation(summary = "仕入伝票登録")
    PurchaseInvoiceResponse createPurchaseInvoice(
        @RequestBody @Valid @NotNull PurchaseInvoiceCreateRequest request
    );
}
