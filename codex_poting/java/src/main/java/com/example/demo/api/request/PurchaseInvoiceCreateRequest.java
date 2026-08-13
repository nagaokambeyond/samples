package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class PurchaseInvoiceCreateRequest {
    @Schema(description = "仕入伝票日付", type = "string", format = "date")
    @NotNull
    LocalDate purchaseInvoiceDate;

    @Schema(description = "仕入先ID", type = "integer", format = "int64")
    @NotNull
    Long supplierId;

    @Schema(description = "入庫店舗ID", type = "integer", format = "int64")
    @NotNull
    Long receivingStoreId;

    @Schema(description = "仕入伝票明細")
    @Valid
    @NotEmpty
    @NotNull
    @Size(max = 10)
    List<PurchaseInvoiceDetailCreateRequest> details;
}
