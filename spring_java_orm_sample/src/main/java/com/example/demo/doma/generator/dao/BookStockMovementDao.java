package com.example.demo.doma.generator.dao;

import com.example.demo.doma.generator.entity.BookStockMovement;
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
public interface BookStockMovementDao {

    /**
     * @param id
     * @return the BookStockMovement entity
     */
    @Select
    BookStockMovement selectById(Long id);

    /**
     * @param id
     * @param version
     * @return the BookStockMovement entity
     */
    @Select(ensureResult = true)
    BookStockMovement selectByIdAndVersion(Long id, Long version);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(BookStockMovement entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(BookStockMovement entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(BookStockMovement entity);
}
