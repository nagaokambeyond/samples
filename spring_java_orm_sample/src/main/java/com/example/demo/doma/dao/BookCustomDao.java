package com.example.demo.doma.dao;

import com.example.demo.doma.generator.entity.Book;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;

@Dao
@ConfigAutowireable
public interface BookCustomDao {
    @Select
    List<Book> selectAll();

    @Select
    List<Book> selectByTitleContainingIgnoreCase(String keyword);

    @Select
    Book selectByIdWithWriteLock(Long id);
}
