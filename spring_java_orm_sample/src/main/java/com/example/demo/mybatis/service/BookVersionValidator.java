package com.example.demo.mybatis.service;

import com.example.demo.mybatis.generator.entity.BookEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Objects;

final class BookVersionValidator {
    private BookVersionValidator() {
    }

    static void validate(BookEntity book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookEntity.class, book.getId());
        }
    }
}
