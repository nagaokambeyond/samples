package com.example.demo.data.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.seasar.doma.Domain;

@Domain(valueType = int.class, factoryMethod = "of")
public enum BookStockMovementSourceType {
    PURCHASE_INVOICE(1),
    SALES_ORDER(2),
    STOCK_ADJUSTMENT(3),
    STORE_TRANSFER(4);

    private final int value;

    BookStockMovementSourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static BookStockMovementSourceType of(int value) {
        for (BookStockMovementSourceType enm : values()) {
            if (enm.value == value) {
                return enm;
            }
        }
        throw new IllegalArgumentException("Unknown BookStockMovementSourceType value: " + value);
    }
}
