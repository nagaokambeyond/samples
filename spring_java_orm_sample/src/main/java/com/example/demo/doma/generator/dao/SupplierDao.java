package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.Supplier;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface SupplierDao {

    /**
     * @param id
     * @return the Supplier entity
     */
    @Select
    Supplier selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the Supplier entity
     */
    @Select(ensureResult = true)
    Supplier selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(Supplier entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(Supplier entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(Supplier entity);
}
