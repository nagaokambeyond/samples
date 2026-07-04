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
public class BookStockMovementListener implements EntityListener<BookStockMovement> {

    @Override
    public void preInsert(BookStockMovement entity, PreInsertContext<BookStockMovement> context) {
    }

    @Override
    public void preUpdate(BookStockMovement entity, PreUpdateContext<BookStockMovement> context) {
    }

    @Override
    public void preDelete(BookStockMovement entity, PreDeleteContext<BookStockMovement> context) {
    }

    @Override
    public void postInsert(BookStockMovement entity, PostInsertContext<BookStockMovement> context) {
    }

    @Override
    public void postUpdate(BookStockMovement entity, PostUpdateContext<BookStockMovement> context) {
    }

    @Override
    public void postDelete(BookStockMovement entity, PostDeleteContext<BookStockMovement> context) {
    }
}
