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
public class PurchaseInvoiceDetailListener implements EntityListener<PurchaseInvoiceDetail> {

    @Override
    public void preInsert(PurchaseInvoiceDetail entity, PreInsertContext<PurchaseInvoiceDetail> context) {
    }

    @Override
    public void preUpdate(PurchaseInvoiceDetail entity, PreUpdateContext<PurchaseInvoiceDetail> context) {
    }

    @Override
    public void preDelete(PurchaseInvoiceDetail entity, PreDeleteContext<PurchaseInvoiceDetail> context) {
    }

    @Override
    public void postInsert(PurchaseInvoiceDetail entity, PostInsertContext<PurchaseInvoiceDetail> context) {
    }

    @Override
    public void postUpdate(PurchaseInvoiceDetail entity, PostUpdateContext<PurchaseInvoiceDetail> context) {
    }

    @Override
    public void postDelete(PurchaseInvoiceDetail entity, PostDeleteContext<PurchaseInvoiceDetail> context) {
    }
}
