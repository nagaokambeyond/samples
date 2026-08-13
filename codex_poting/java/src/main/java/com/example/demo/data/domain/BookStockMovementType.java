package com.example.demo.data.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.seasar.doma.Domain;

@Domain(valueType = int.class, factoryMethod = "of")
public enum BookStockMovementType {
    INITIAL_STOCK(1),
    PURCHASE(2),
    SALE(3),
    RETURN_PURCHASE(4),
    SALES_RETURN(5),
    STOCK_ADJUSTMENT(6),
    STORE_TRANSFER_IN(7),
    STORE_TRANSFER_OUT(8);

    private final int value;

    BookStockMovementType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static BookStockMovementType of(int value) {
        for (BookStockMovementType enm : values()) {
            if (enm.value == value) {
                return enm;
            }
        }
        throw new IllegalArgumentException("Unknown BookStockMovementType value: " + value);
    }
}
