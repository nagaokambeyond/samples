package com.example.demo.data.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.seasar.doma.Domain;

@Domain(valueType = int.class, factoryMethod = "of")
public enum PurchaseInvoiceType {
    PURCHASE(1),
    RETURN_PURCHASE(2);

    private final int value;

    PurchaseInvoiceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static PurchaseInvoiceType of(int value) {
        for (PurchaseInvoiceType enm : values()) {
            if (enm.value == value) {
                return enm;
            }
        }
        throw new IllegalArgumentException("Unknown PurchaseInvoiceType value: " + value);
    }
}
