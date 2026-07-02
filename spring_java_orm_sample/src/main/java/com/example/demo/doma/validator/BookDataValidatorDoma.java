package com.example.demo.doma.validator;

import com.example.demo.doma.dao.BookCustomDao;
import com.example.demo.doma.generator.dao.BookGenreDao;
import com.example.demo.doma.generator.dao.PublisherDao;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.doma.generator.entity.BookGenre;
import com.example.demo.doma.generator.entity.Publisher;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("doma")
@RequiredArgsConstructor
public class BookDataValidatorDoma {
    private final PublisherDao publisherDao;
    private final BookGenreDao bookGenreDao;
    private final BookCustomDao bookCustomDao;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        final var publisher = publisherDao.selectById(publisherId);
        if (Objects.isNull(publisher)) {
            throw new ForeignKeyReferenceNotFoundException(Publisher.class, publisherId);
        }

        final var bookGenre = bookGenreDao.selectById(genreId);
        if (Objects.isNull(bookGenre)) {
            throw new ForeignKeyReferenceNotFoundException(BookGenre.class, genreId);
        }
    }

    public void versionValidate(Book book, Long requestVersion) {
        if (!Objects.equals(book.getVersion(), requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId());
        }
    }

    public void uniqueIsbnValidate(String isbn, Long bookId) {
        final var book = bookCustomDao.selectByIsbn(isbn);
        if (Objects.nonNull(book) && !Objects.equals(book.getId(), bookId)) {
            throw new UniqueConstraintValidationException("book", "isbn", isbn);
        }
    }
}
