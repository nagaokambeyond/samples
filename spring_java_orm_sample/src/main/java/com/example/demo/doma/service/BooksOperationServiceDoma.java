package com.example.demo.doma.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.doma.converter.BookOperationConverterDoma;
import com.example.demo.doma.dao.BookCustomDao;
import com.example.demo.doma.generator.dao.BookDao;
import com.example.demo.doma.generator.entity.Book;
import com.example.demo.doma.validator.BookDataValidatorDoma;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.service.BooksOperationService;
import com.example.demo.service.PageCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.OptimisticLockException;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Profile("doma")
@Primary
@RequiredArgsConstructor
public class BooksOperationServiceDoma implements BooksOperationService {
    private final BookDao bookDao;
    private final BookCustomDao bookCustomDao;
    private final BookOperationConverterDoma converter;
    private final BookDataValidatorDoma dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return converter.toResponse(findByIdWithPublisherName(id));
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var offset = PageCalculator.calculateOffset(page, size);
        final var books = bookCustomDao.selectByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo, size, offset);
        final var totalElements = bookCustomDao.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo);
        final var response = new BookPageResponse();
        response.setContent(converter.toResponse(books));
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(PageCalculator.calculateTotalPages(totalElements, size));
        return response;
    }

    @Transactional
    @Override
    public BookResponse create(@NonNull BookCreateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var now = LocalDateTime.now();
        final var book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setCreateAt(now);
        book.setUpdateAt(now);
        book.setVersion(1L);

        bookDao.insert(book);
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var book = bookCustomDao.selectByIdWithWriteLock(request.getId());
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        dataValidator.versionValidate(book, request.getVersion());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setUpdateAt(LocalDateTime.now());

        try {
            bookDao.update(book);
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId(), ex);
        }
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookCustomDao.selectByIdWithWriteLock(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        try {
            bookDao.delete(book);
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Book.class, book.getId(), ex);
        }
    }

    private com.example.demo.doma.entity.BookWithPublisherName findByIdWithPublisherName(Long id) {
        final var book = bookCustomDao.selectByIdWithPublisherName(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }
}
