package com.example.demo.data.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.seasar.doma.Domain;

@Domain(valueType = int.class, factoryMethod = "of")
public enum PurchaseOrderType {
    PURCHASE(1),
    RETURN_PURCHASE(2);

    private final int value;

    PurchaseOrderType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static PurchaseOrderType of(int value) {
        for (PurchaseOrderType enm : values()) {
            if (enm.value == value) {
                return enm;
            }
        }
        throw new IllegalArgumentException("Unknown PurchaseOrderType value: " + value);
    }
}
