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
public class BookStockListener implements EntityListener<BookStock> {

    @Override
    public void preInsert(BookStock entity, PreInsertContext<BookStock> context) {
    }

    @Override
    public void preUpdate(BookStock entity, PreUpdateContext<BookStock> context) {
    }

    @Override
    public void preDelete(BookStock entity, PreDeleteContext<BookStock> context) {
    }

    @Override
    public void postInsert(BookStock entity, PostInsertContext<BookStock> context) {
    }

    @Override
    public void postUpdate(BookStock entity, PostUpdateContext<BookStock> context) {
    }

    @Override
    public void postDelete(BookStock entity, PostDeleteContext<BookStock> context) {
    }
}
