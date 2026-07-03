package com.example.demo.api.request;

import com.example.demo.api.validator.Isbn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class PurchaseInvoiceDetailCreateRequest {
    @Schema(description = "仕入伝票明細ISBN", type = "string", example = "0000000000001")
    @NotNull
    @Isbn
    String purchaseInvoiceDetailIsbn;

    @Schema(description = "仕入伝票明細単価")
    @Max(10000)
    @Min(1)
    @NotNull
    Integer purchaseInvoiceDetailUnitPrice;

    @Schema(description = "仕入伝票明細数量")
    @Max(1000)
    @Min(1)
    @NotNull
    Integer purchaseInvoiceDetailQuantity;
}
