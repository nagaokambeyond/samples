package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.PurchaseOrderDetail;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface PurchaseOrderDetailDao {

    /**
     * @param id
     * @return the PurchaseOrderDetail entity
     */
    @Select
    PurchaseOrderDetail selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the PurchaseOrderDetail entity
     */
    @Select(ensureResult = true)
    PurchaseOrderDetail selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(PurchaseOrderDetail entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(PurchaseOrderDetail entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(PurchaseOrderDetail entity);
}
