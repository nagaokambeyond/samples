package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.Store;
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
public interface StoreDao {

    /**
     * @param id
     * @return the Store entity
     */
    @Select
    Store selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the Store entity
     */
    @Select(ensureResult = true)
    Store selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(Store entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(Store entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(Store entity);
}
