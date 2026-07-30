package com.example.demo.data.domain

import com.fasterxml.jackson.annotation.JsonCreator
import org.seasar.doma.Domain

@Domain(valueType = Int::class, factoryMethod = "of")
enum class BookStockMovementSourceType(val value: Int) {
    PURCHASE_INVOICE(1),
    SALES_ORDER(2),
    STOCK_ADJUSTMENT(3),
    STORE_TRANSFER(4);

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): BookStockMovementSourceType {
            for (enm in entries) {
                if (enm.value == value) {
                    return enm
                }
            }
            throw IllegalArgumentException("Unknown BookStockMovementSourceType value: " + value)
        }
    }
}
