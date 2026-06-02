package com.example.demo.jpa.service;

import com.example.demo.jpa.entity.Book;
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
