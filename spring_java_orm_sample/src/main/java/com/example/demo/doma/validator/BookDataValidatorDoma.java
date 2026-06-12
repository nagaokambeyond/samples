package com.example.demo.doma.validator;

import com.example.demo.doma.generator.dao.BookGenreDao;
import com.example.demo.doma.generator.dao.PublisherDao;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookDataValidatorDoma {
    private final PublisherDao publisherDao;
    private final BookGenreDao bookGenreDao;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        final var publisher = publisherDao.selectById(publisherId);
        final var bookGenre = bookGenreDao.selectById(genreId);
        if (Objects.isNull(publisher) || Objects.isNull(bookGenre)) {
            throw new ForeignKeyReferenceNotFoundException();
        }
    }

    public void versionValidate(Book book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId());
        }
    }
}
