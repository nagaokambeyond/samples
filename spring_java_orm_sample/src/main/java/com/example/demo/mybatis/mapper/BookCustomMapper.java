package com.example.demo.mybatis.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookCustomMapper {
    List<BookEntity> selectByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    BookEntity selectByPrimaryKeyWithWriteLock(@Param("id") Long id);

    int insertWithGeneratedKey(BookEntity row);
}
