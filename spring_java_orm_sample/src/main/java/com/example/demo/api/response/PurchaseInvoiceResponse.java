package com.example.demo.api.response;

import com.example.demo.data.domain.PurchaseInvoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
public class PurchaseInvoiceResponse {
    @Schema(description = "仕入伝票ID", type = "integer", format = "int64")
    @NotNull
    Long id;

    @Schema(description = "仕入伝票種別")
    @NotNull
    PurchaseInvoiceType purchaseInvoiceType;

    @Schema(description = "返品元仕入伝票ID", type = "integer", format = "int64")
    Long returnPurchaseInvoiceId;

    @Schema(description = "仕入伝票日付", type = "string", format = "date")
    @NotNull
    LocalDate purchaseInvoiceDate;

    @Schema(description = "仕入先ID", type = "integer", format = "int64")
    @NotNull
    Long supplierId;

    @Schema(description = "入庫店舗ID", type = "integer", format = "int64")
    @NotNull
    Long receivingStoreId;

    @Schema(description = "仕入伝票金額", type = "integer", format = "int64")
    @NotNull
    Long purchaseInvoiceAmount;

    @Schema(description = "更新日時", type = "string", format = "date-time")
    @NotNull
    LocalDateTime updateAt;

    @Schema(description = "バージョン", type = "integer", format = "int64")
    @NotNull
    Long version;

    @Schema(description = "仕入伝票明細")
    @NotEmpty
    List<PurchaseInvoiceDetailResponse> detail;
}
