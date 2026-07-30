package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.BookStock;
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
public interface BookStockDao {

    /**
     * @param id
     * @return the BookStock entity
     */
    @Select
    BookStock selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the BookStock entity
     */
    @Select(ensureResult = true)
    BookStock selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(BookStock entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(BookStock entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(BookStock entity);
}
