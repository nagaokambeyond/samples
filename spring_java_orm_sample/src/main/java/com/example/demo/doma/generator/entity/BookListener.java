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
public class BookListener implements EntityListener<Book> {

    @Override
    public void preInsert(Book entity, PreInsertContext<Book> context) {
    }

    @Override
    public void preUpdate(Book entity, PreUpdateContext<Book> context) {
    }

    @Override
    public void preDelete(Book entity, PreDeleteContext<Book> context) {
    }

    @Override
    public void postInsert(Book entity, PostInsertContext<Book> context) {
    }

    @Override
    public void postUpdate(Book entity, PostUpdateContext<Book> context) {
    }

    @Override
    public void postDelete(Book entity, PostDeleteContext<Book> context) {
    }
}
