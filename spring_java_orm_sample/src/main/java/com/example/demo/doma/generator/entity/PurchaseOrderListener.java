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
public class PurchaseOrderListener implements EntityListener<PurchaseOrder> {

    @Override
    public void preInsert(PurchaseOrder entity, PreInsertContext<PurchaseOrder> context) {
    }

    @Override
    public void preUpdate(PurchaseOrder entity, PreUpdateContext<PurchaseOrder> context) {
    }

    @Override
    public void preDelete(PurchaseOrder entity, PreDeleteContext<PurchaseOrder> context) {
    }

    @Override
    public void postInsert(PurchaseOrder entity, PostInsertContext<PurchaseOrder> context) {
    }

    @Override
    public void postUpdate(PurchaseOrder entity, PostUpdateContext<PurchaseOrder> context) {
    }

    @Override
    public void postDelete(PurchaseOrder entity, PostDeleteContext<PurchaseOrder> context) {
    }
}
