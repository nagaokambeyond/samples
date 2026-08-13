package com.example.demo.doma.generator.entity;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

/**
 * 
 */
public class BookSalesUnitPriceHistoryListener implements EntityListener<BookSalesUnitPriceHistory> {

    @Override
    public void preInsert(BookSalesUnitPriceHistory entity, PreInsertContext<BookSalesUnitPriceHistory> context) {
    }

    @Override
    public void preUpdate(BookSalesUnitPriceHistory entity, PreUpdateContext<BookSalesUnitPriceHistory> context) {
    }

    @Override
    public void preDelete(BookSalesUnitPriceHistory entity, PreDeleteContext<BookSalesUnitPriceHistory> context) {
    }

    @Override
    public void postInsert(BookSalesUnitPriceHistory entity, PostInsertContext<BookSalesUnitPriceHistory> context) {
    }

    @Override
    public void postUpdate(BookSalesUnitPriceHistory entity, PostUpdateContext<BookSalesUnitPriceHistory> context) {
    }

    @Override
    public void postDelete(BookSalesUnitPriceHistory entity, PostDeleteContext<BookSalesUnitPriceHistory> context) {
    }
}
