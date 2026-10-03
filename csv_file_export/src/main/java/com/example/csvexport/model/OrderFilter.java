package com.example.csvexport.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderFilter(String status, LocalDate orderDateFrom, LocalDate orderDateTo,
                          String customerName, BigDecimal amountMin, BigDecimal amountMax) {
    public static OrderFilter empty() {
        return new OrderFilter(null, null, null, null, null, null);
    }
}
