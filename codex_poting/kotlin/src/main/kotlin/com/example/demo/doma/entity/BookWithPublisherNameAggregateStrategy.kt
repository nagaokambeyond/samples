package com.example.demo.doma.entity

import org.seasar.doma.AggregateStrategy
import org.seasar.doma.AssociationLinker
import java.util.*
import java.util.function.BiFunction

@AggregateStrategy(root = BookWithPublisherName::class, tableAlias = "b")
interface BookWithPublisherNameAggregateStrategy {
    companion object {
        @JvmField
        @AssociationLinker(propertyPath = "bookStockList", tableAlias = "bs")
        val bookStockList: BiFunction<BookWithPublisherName?, BookStockWithStoreName?, BookWithPublisherName?> =
            BiFunction { book: BookWithPublisherName?, bookStock: BookStockWithStoreName? ->
                if (Objects.nonNull(bookStock) && Objects.nonNull(bookStock!!.id)) {
                    book!!.bookStockList!!.add(bookStock)
                }
                book
            }
    }
}
