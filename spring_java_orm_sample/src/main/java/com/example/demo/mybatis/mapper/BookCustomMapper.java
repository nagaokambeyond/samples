package com.example.demo.mybatis.mapper;

import com.example.demo.mybatis.generator.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BookCustomMapper {
    List<BookEntity> selectByTitleContainingIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo,
        @Param("limit") int limit,
        @Param("offset") long offset
    );

    long countByTitleContainingIgnoreCase(
        @Param("keyword") String keyword,
        @Param("releaseDateFrom") LocalDate releaseDateFrom,
        @Param("releaseDateTo") LocalDate releaseDateTo
    );

    BookEntity selectByPrimaryKeyWithWriteLock(@Param("id") Long id);

    void insertWithGeneratedKey(BookEntity row);
}
