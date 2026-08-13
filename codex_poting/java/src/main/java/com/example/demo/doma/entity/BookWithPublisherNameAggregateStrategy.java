package com.example.demo.doma.entity;

import org.seasar.doma.AggregateStrategy;
import org.seasar.doma.AssociationLinker;

import java.util.Objects;
import java.util.function.BiFunction;

@AggregateStrategy(root = BookWithPublisherName.class, tableAlias = "b")
public interface BookWithPublisherNameAggregateStrategy {
    @AssociationLinker(propertyPath = "bookStockList", tableAlias = "bs")
    BiFunction<BookWithPublisherName, BookStockWithStoreName, BookWithPublisherName> bookStockList =
        (book, bookStock) -> {
            if (Objects.nonNull(bookStock) && Objects.nonNull(bookStock.getId())) {
                book.getBookStockList().add(bookStock);
            }
            return book;
        };
}
