package com.example.demo.data.domain

import com.fasterxml.jackson.annotation.JsonCreator
import org.seasar.doma.Domain

@Domain(valueType = Int::class, factoryMethod = "of")
enum class PurchaseInvoiceType(val value: Int) {
    PURCHASE(1),
    RETURN_PURCHASE(2);

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): PurchaseInvoiceType {
            for (enm in entries) {
                if (enm.value == value) {
                    return enm
                }
            }
            throw IllegalArgumentException("Unknown PurchaseInvoiceType value: " + value)
        }
    }
}
