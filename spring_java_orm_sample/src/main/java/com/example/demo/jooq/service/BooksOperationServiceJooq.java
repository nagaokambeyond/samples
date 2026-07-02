package com.example.demo.jooq.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.response.BookResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.jooq.converter.BookOperationConverterJooq;
import com.example.demo.jooq.dsl.BookOperationDsl;
import com.example.demo.jooq.validator.BookDataValidatorJooq;
import com.example.demo.service.BooksOperationService;
import com.example.demo.service.PageCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.demo.jooq.generated.Tables.BOOK;

@Service
@Profile("jooq")
@RequiredArgsConstructor
public class BooksOperationServiceJooq implements BooksOperationService {
    private final BookOperationDsl bookOperationDsl;
    private final BookOperationConverterJooq converter;
    private final BookDataValidatorJooq dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        final var rows = bookOperationDsl.selectByIdWithPublisherName(id);
        if (rows.isEmpty()) {
            throw new RepositoryDataNotfoundException();
        }
        return converter.toResponse(rows);
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var offset = PageCalculator.calculateOffset(page, size);
        final var condition = bookOperationDsl.searchCondition(keyword, releaseDateFrom, releaseDateTo);
        final var rows = bookOperationDsl.selectByTitleOrAuthorStartingWithIgnoreCase(condition, size, offset);
        final long totalElements = bookOperationDsl.totalElements(condition);

        final var response = new BookPageResponse();
        response.setContent(converter.toResponseList(rows));
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
        dataValidator.uniqueIsbnValidate(request.getIsbn(), null);

        final var id = bookOperationDsl.insert(request);
        return findById(Objects.requireNonNull(id));
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var book = bookOperationDsl.selectBookForUpdate(request.getId());
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        final var currentVersion = book.get(BOOK.VERSION);
        dataValidator.versionValidate(request.getId(), currentVersion, request.getVersion());
        dataValidator.uniqueIsbnValidate(request.getIsbn(), request.getId());
        bookOperationDsl.update(request, currentVersion);

        return findById(request.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookOperationDsl.selectBookForUpdate(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        bookOperationDsl.delete(id);
    }
}
