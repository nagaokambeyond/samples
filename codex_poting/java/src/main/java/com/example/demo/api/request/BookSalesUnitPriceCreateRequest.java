package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BookSalesUnitPriceCreateRequest {
    @NotNull
    @Min(1)
    @Max(10000)
    @Schema(description = "販売単価", type = "integer", example = "1500")
    Integer salesUnitPrice;

    @NotNull
    @Future
    @Schema(description = "有効開始日", type = "string", format = "date")
    LocalDate effectiveFrom;
}
