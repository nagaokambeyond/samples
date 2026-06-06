package com.example.demo.doma.service;

import com.example.demo.doma.generator.entity.Book;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Objects;

final class BookVersionValidator {
    private BookVersionValidator() {
    }

    static void validate(Book book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId());
        }
    }
}
