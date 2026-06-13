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
public class SupplierListener implements EntityListener<Supplier> {

    @Override
    public void preInsert(Supplier entity, PreInsertContext<Supplier> context) {
    }

    @Override
    public void preUpdate(Supplier entity, PreUpdateContext<Supplier> context) {
    }

    @Override
    public void preDelete(Supplier entity, PreDeleteContext<Supplier> context) {
    }

    @Override
    public void postInsert(Supplier entity, PostInsertContext<Supplier> context) {
    }

    @Override
    public void postUpdate(Supplier entity, PostUpdateContext<Supplier> context) {
    }

    @Override
    public void postDelete(Supplier entity, PostDeleteContext<Supplier> context) {
    }
}
