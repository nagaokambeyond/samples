package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.Publisher;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

/**
 */
@Dao
public interface PublisherDao {

    /**
     * @param id
     * @return the Publisher entity
     */
    @Select
    Publisher selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the Publisher entity
     */
    @Select(ensureResult = true)
    Publisher selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(Publisher entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(Publisher entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(Publisher entity);
}
