package com.example.demo.jooq.entity;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BookSalesUnitPriceHistoryRow {
    Long id;
    LocalDate effectiveFrom;
    Long version;
}
