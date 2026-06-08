package com.example.demo.jpa.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookDataValidatorJPA {
    private final PublisherRepository publisherRepository;

    public void foreignKeyValidate(Long publisherId) {
        if (!publisherRepository.existsById(publisherId)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }

    public void versionValidate(Book book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId());
        }
    }
}
