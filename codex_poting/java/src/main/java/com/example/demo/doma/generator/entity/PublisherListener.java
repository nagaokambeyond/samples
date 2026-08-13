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
public class PublisherListener implements EntityListener<Publisher> {

    @Override
    public void preInsert(Publisher entity, PreInsertContext<Publisher> context) {
    }

    @Override
    public void preUpdate(Publisher entity, PreUpdateContext<Publisher> context) {
    }

    @Override
    public void preDelete(Publisher entity, PreDeleteContext<Publisher> context) {
    }

    @Override
    public void postInsert(Publisher entity, PostInsertContext<Publisher> context) {
    }

    @Override
    public void postUpdate(Publisher entity, PostUpdateContext<Publisher> context) {
    }

    @Override
    public void postDelete(Publisher entity, PostDeleteContext<Publisher> context) {
    }
}
