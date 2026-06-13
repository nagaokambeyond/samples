package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.PurchaseOrder;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface PurchaseOrderDao {

    /**
     * @param id
     * @return the PurchaseOrder entity
     */
    @Select
    PurchaseOrder selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the PurchaseOrder entity
     */
    @Select(ensureResult = true)
    PurchaseOrder selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(PurchaseOrder entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(PurchaseOrder entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(PurchaseOrder entity);
}
