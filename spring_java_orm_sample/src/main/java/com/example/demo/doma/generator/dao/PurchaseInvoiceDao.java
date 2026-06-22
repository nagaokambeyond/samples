package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.PurchaseInvoice;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

/**
 */
@Dao
@ConfigAutowireable
public interface PurchaseInvoiceDao {

    /**
     * @param id
     * @return the PurchaseInvoice entity
     */
    @Select
    PurchaseInvoice selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the PurchaseInvoice entity
     */
    @Select(ensureResult = true)
    PurchaseInvoice selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(PurchaseInvoice entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(PurchaseInvoice entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(PurchaseInvoice entity);
}
