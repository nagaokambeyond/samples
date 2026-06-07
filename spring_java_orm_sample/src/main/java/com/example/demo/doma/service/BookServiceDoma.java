package com.example.demo.doma.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.converter.BookConverter;
import com.example.demo.doma.dao.BookCustomDao;
import com.example.demo.doma.generator.dao.BookDao;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.doma.validator.BookDataValidatorDoma;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.service.BookService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.OptimisticLockException;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class BookServiceDoma implements BookService {
    private final BookDao bookDao;
    private final BookCustomDao bookCustomDao;
    private final BookConverter converter;
    private final BookDataValidatorDoma dataValidator;

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> findAll() {
        return converter.toResponseFromDomaBooks(bookCustomDao.selectAll());
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return converter.toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> search(@NonNull String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        return converter.toResponseFromDomaBooks(bookCustomDao.selectByTitleContainingIgnoreCase(keyword, releaseDateFrom, releaseDateTo));
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        final var now = LocalDateTime.now();
        final var book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setCreateAt(now);
        book.setUpdateAt(now);
        book.setVersion(1L);

        bookDao.insert(book);
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId());

        final var book = bookCustomDao.selectByIdWithWriteLock(request.getId());
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }

        BookVersionValidator.validate(book, request.getVersion());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setUpdateAt(LocalDateTime.now());

        try {
            bookDao.update(book);
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId(), ex);
        }
        return converter.toResponse(book);
    }

    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookCustomDao.selectByIdWithWriteLock(id);
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }

        try {
            bookDao.delete(book);
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId(), ex);
        }
    }

    private Book findEntityById(Long id) {
        final var book = bookDao.selectById(id);
        if (book == null) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }
}
