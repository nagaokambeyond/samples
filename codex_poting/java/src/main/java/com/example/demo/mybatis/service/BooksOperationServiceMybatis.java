package com.example.demo.mybatis.service;

import com.example.demo.api.response.BookPageResponse;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.mybatis.converter.BookOperationConverterMybatis;
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntity;
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntityExample;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.mapper.BookSalesUnitPriceHistoryMapper;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.mapper.BookCustomMapper;
import com.example.demo.mybatis.validator.BookDataValidatorMybatis;
import com.example.demo.service.BooksOperationService;
import com.example.demo.util.PageCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Profile("mybatis")
@RequiredArgsConstructor
public class BooksOperationServiceMybatis implements BooksOperationService {
    private final BookMapper bookMapper;
    private final BookSalesUnitPriceHistoryMapper bookSalesUnitPriceHistoryMapper;
    private final BookCustomMapper bookCustomMapper;
    private final BookOperationConverterMybatis converter;
    private final BookDataValidatorMybatis dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        return converter.toResponse(findByIdWithPublisherName(id));
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var offset = PageCalculator.calculateOffset(page, size);
        final var books = bookCustomMapper.selectByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo, size, offset);
        final var totalElements = bookCustomMapper.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo);
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
        dataValidator.uniqueIsbnValidate(request.getIsbn(), null);

        final var now = LocalDateTime.now();
        final var book = new BookEntity();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setIsbn(request.getIsbn());
        book.setCreateAt(now);
        book.setUpdateAt(now);
        book.setVersion(1L);

        bookCustomMapper.insertWithGeneratedKey(book);
        bookCustomMapper.insertSalesUnitPriceHistoryWithId(toSalesUnitPriceHistory(book.getId(), request.getSalesUnitPrice(), request.getReleaseDate(), null, now));
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(request.getId());
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        dataValidator.versionValidate(book, request.getVersion());
        dataValidator.uniqueIsbnValidate(request.getIsbn(), book.getId());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setReleaseDate(request.getReleaseDate());
        book.setPublisherId(request.getPublisherId());
        book.setGenreId(request.getGenreId());
        book.setIsbn(request.getIsbn());
        book.setUpdateAt(LocalDateTime.now());
        book.setVersion(book.getVersion() + 1);

        bookMapper.updateByPrimaryKey(book);
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void createSalesUnitPrice(@NonNull Long bookId, @NonNull BookSalesUnitPriceCreateRequest request) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(bookId);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        final var followingHistories = selectFollowingHistories(book.getId(), request.getEffectiveFrom());
        if (!followingHistories.isEmpty() && followingHistories.getFirst().getEffectiveFrom().equals(request.getEffectiveFrom())) {
            throw new UniqueConstraintValidationException(
                "book_sales_unit_price_history",
                "book_id,effective_from",
                book.getId() + "," + request.getEffectiveFrom()
            );
        }

        final var previousHistory = selectPreviousHistory(book.getId(), request.getEffectiveFrom());
        if (Objects.isNull(previousHistory)) {
            throw new RepositoryDataNotfoundException();
        }

        final var now = LocalDateTime.now();
        previousHistory.setEffectiveTo(request.getEffectiveFrom().minusDays(1));
        previousHistory.setUpdateAt(now);
        previousHistory.setVersion(previousHistory.getVersion() + 1);
        bookSalesUnitPriceHistoryMapper.updateByPrimaryKey(previousHistory);

        final var effectiveTo = followingHistories.isEmpty() ? null : followingHistories.getFirst().getEffectiveFrom().minusDays(1);
        bookCustomMapper.insertSalesUnitPriceHistoryWithId(toSalesUnitPriceHistory(book.getId(), request.getSalesUnitPrice(), request.getEffectiveFrom(), effectiveTo, now));
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }

        bookMapper.deleteByPrimaryKey(book.getId());
    }

    private com.example.demo.mybatis.entity.BookWithPublisherName findByIdWithPublisherName(Long id) {
        final var book = bookCustomMapper.selectByPrimaryKeyWithPublisherName(id);
        if (Objects.isNull(book)) {
            throw new RepositoryDataNotfoundException();
        }
        return book;
    }

    private BookSalesUnitPriceHistoryEntity toSalesUnitPriceHistory(Long bookId, Integer salesUnitPrice, LocalDate effectiveFrom, LocalDate effectiveTo, LocalDateTime now) {
        final var history = new BookSalesUnitPriceHistoryEntity();
        history.setId(bookCustomMapper.selectNextSalesUnitPriceHistoryId());
        history.setBookId(bookId);
        history.setSalesUnitPrice(salesUnitPrice);
        history.setEffectiveFrom(effectiveFrom);
        history.setEffectiveTo(effectiveTo);
        history.setCreateAt(now);
        history.setUpdateAt(now);
        history.setVersion(1L);
        return history;
    }

    private List<BookSalesUnitPriceHistoryEntity> selectFollowingHistories(Long bookId, LocalDate effectiveFrom) {
        final var example = new BookSalesUnitPriceHistoryEntityExample();
        example.createCriteria()
            .andBookIdEqualTo(bookId)
            .andEffectiveFromGreaterThanOrEqualTo(effectiveFrom);
        example.setOrderByClause("effective_from");
        return bookSalesUnitPriceHistoryMapper.selectByExample(example);
    }

    private BookSalesUnitPriceHistoryEntity selectPreviousHistory(Long bookId, LocalDate effectiveFrom) {
        final var example = new BookSalesUnitPriceHistoryEntityExample();
        example.createCriteria()
            .andBookIdEqualTo(bookId)
            .andEffectiveFromLessThan(effectiveFrom);
        example.setOrderByClause("effective_from desc");
        final var histories = bookSalesUnitPriceHistoryMapper.selectByExample(example);
        return histories.isEmpty() ? null : histories.getFirst();
    }

}
