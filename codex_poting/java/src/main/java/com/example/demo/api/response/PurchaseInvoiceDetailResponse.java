package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseInvoiceDetailResponse {
    @Schema(description = "仕入伝票明細ID", type = "integer", format = "int64")
    @NotNull
    Long id;

    @Schema(description = "仕入伝票ID", type = "integer", format = "int64")
    @NotNull
    Long purchaseInvoiceId;

    @Schema(description = "仕入伝票明細本ID", type = "integer", format = "int64")
    @NotNull
    Long purchaseInvoiceDetailBookId;

    @Schema(description = "仕入伝票明細単価")
    @NotNull
    Integer purchaseInvoiceDetailUnitPrice;

    @Schema(description = "仕入伝票明細数量")
    @NotNull
    Integer purchaseInvoiceDetailQuantity;

    @Schema(description = "仕入伝票明細金額", type = "integer", format = "int64")
    @NotNull
    Long purchaseInvoiceDetailAmount;

    @Schema(description = "更新日時", type = "string", format = "date-time")
    @NotNull
    LocalDateTime updateAt;

    @Schema(description = "バージョン", type = "integer", format = "int64")
    @NotNull
    Long version;
}
