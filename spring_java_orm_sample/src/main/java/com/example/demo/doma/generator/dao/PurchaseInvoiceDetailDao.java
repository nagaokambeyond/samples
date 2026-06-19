package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.PurchaseInvoiceDetail;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface PurchaseInvoiceDetailDao {

    /**
     * @param id
     * @return the PurchaseInvoiceDetail entity
     */
    @Select
    PurchaseInvoiceDetail selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the PurchaseInvoiceDetail entity
     */
    @Select(ensureResult = true)
    PurchaseInvoiceDetail selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(PurchaseInvoiceDetail entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(PurchaseInvoiceDetail entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(PurchaseInvoiceDetail entity);
}
