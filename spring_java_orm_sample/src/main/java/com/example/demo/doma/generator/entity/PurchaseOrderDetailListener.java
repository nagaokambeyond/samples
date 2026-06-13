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
public class PurchaseOrderDetailListener implements EntityListener<PurchaseOrderDetail> {

    @Override
    public void preInsert(PurchaseOrderDetail entity, PreInsertContext<PurchaseOrderDetail> context) {
    }

    @Override
    public void preUpdate(PurchaseOrderDetail entity, PreUpdateContext<PurchaseOrderDetail> context) {
    }

    @Override
    public void preDelete(PurchaseOrderDetail entity, PreDeleteContext<PurchaseOrderDetail> context) {
    }

    @Override
    public void postInsert(PurchaseOrderDetail entity, PostInsertContext<PurchaseOrderDetail> context) {
    }

    @Override
    public void postUpdate(PurchaseOrderDetail entity, PostUpdateContext<PurchaseOrderDetail> context) {
    }

    @Override
    public void postDelete(PurchaseOrderDetail entity, PostDeleteContext<PurchaseOrderDetail> context) {
    }
}
