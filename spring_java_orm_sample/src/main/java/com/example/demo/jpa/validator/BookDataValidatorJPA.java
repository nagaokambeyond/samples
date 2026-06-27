package com.example.demo.jpa.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.entity.BookGenre;
import com.example.demo.jpa.entity.Publisher;
import com.example.demo.jpa.repository.BookGenreRepository;
import com.example.demo.jpa.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("jpa")
@RequiredArgsConstructor
public class BookDataValidatorJPA {
    private final PublisherRepository publisherRepository;
    private final BookGenreRepository bookGenreRepository;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        if (!publisherRepository.existsById(publisherId)) {
            throw new ForeignKeyReferenceNotFoundException(Publisher.class, publisherId);
        }
        if (!bookGenreRepository.existsById(genreId)) {
            throw new ForeignKeyReferenceNotFoundException(BookGenre.class, genreId);
        }
    }

    public void versionValidate(Book book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId());
        }
    }
}
