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
public class PurchaseInvoiceListener implements EntityListener<PurchaseInvoice> {

    @Override
    public void preInsert(PurchaseInvoice entity, PreInsertContext<PurchaseInvoice> context) {
    }

    @Override
    public void preUpdate(PurchaseInvoice entity, PreUpdateContext<PurchaseInvoice> context) {
    }

    @Override
    public void preDelete(PurchaseInvoice entity, PreDeleteContext<PurchaseInvoice> context) {
    }

    @Override
    public void postInsert(PurchaseInvoice entity, PostInsertContext<PurchaseInvoice> context) {
    }

    @Override
    public void postUpdate(PurchaseInvoice entity, PostUpdateContext<PurchaseInvoice> context) {
    }

    @Override
    public void postDelete(PurchaseInvoice entity, PostDeleteContext<PurchaseInvoice> context) {
    }
}
