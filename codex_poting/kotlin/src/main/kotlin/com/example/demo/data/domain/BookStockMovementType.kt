package com.example.demo.data.domain

import com.fasterxml.jackson.annotation.JsonCreator
import org.seasar.doma.Domain

@Domain(valueType = Int::class, factoryMethod = "of")
enum class BookStockMovementType(val value: Int) {
    INITIAL_STOCK(1),
    PURCHASE(2),
    SALE(3),
    RETURN_PURCHASE(4),
    SALES_RETURN(5),
    STOCK_ADJUSTMENT(6),
    STORE_TRANSFER_IN(7),
    STORE_TRANSFER_OUT(8);

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): BookStockMovementType {
            for (enm in entries) {
                if (enm.value == value) {
                    return enm
                }
            }
            throw IllegalArgumentException("Unknown BookStockMovementType value: " + value)
        }
    }
}
