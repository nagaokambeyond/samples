package com.example.demo.jpa.service;

import com.example.demo.api.request.BookSalesUnitPriceCreateRequest;
import com.example.demo.api.response.BookPageResponse;
import com.example.demo.config.RetryableOnLockFailure;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.jpa.converter.BookOperationConverterJPA;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.api.response.BookResponse;
import com.example.demo.jpa.repository.BookSalesUnitPriceHistoryRepository;
import com.example.demo.jpa.validator.BookDataValidatorJPA;
import com.example.demo.service.BooksOperationService;
import com.example.demo.util.PageCalculator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Profile("jpa")
@RequiredArgsConstructor
public class BooksOperationServiceJPA implements BooksOperationService {
    private final BookRepository bookRepository;
    private final BookSalesUnitPriceHistoryRepository bookSalesUnitPriceHistoryRepository;
    private final BookOperationConverterJPA converter;
    private final BookDataValidatorJPA dataValidator;

    @Transactional(readOnly = true)
    @Override
    public BookResponse findById(@NonNull Long id) {
        final var books = bookRepository.findByIdWithPublisherName(id);
        if (books.isEmpty()) {
            throw new RepositoryDataNotfoundException();
        }
        return converter.toResponseFrom(books);
    }

    @Transactional(readOnly = true)
    @Override
    public BookPageResponse search(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo, int page, int size) {
        final var offset = PageCalculator.calculateOffset(page, size);
        final var books = bookRepository.findByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo, size, offset);
        final var totalElements = bookRepository.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo);
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

        final var book = bookRepository.save(new Book(null, request.getTitle(), request.getAuthor(), request.getReleaseDate(), request.getPublisherId(), request.getGenreId(), request.getIsbn(), null, null, 1L));
        insertSalesUnitPriceHistory(book.getId(), request.getSalesUnitPrice(), request.getReleaseDate(), null);
        return findById(book.getId());
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public BookResponse update(@NonNull BookUpdateRequest request) {
        dataValidator.foreignKeyValidate(request.getPublisherId(), request.getGenreId());

        return bookRepository.findByIdWithWriteLock(request.getId())
            .map(b -> {
                dataValidator.versionValidate(b, request.getVersion());
                dataValidator.uniqueIsbnValidate(request.getIsbn(), b.getId());
                b.setTitle(request.getTitle());
                b.setAuthor(request.getAuthor());
                b.setReleaseDate(request.getReleaseDate());
                b.setPublisherId(request.getPublisherId());
                b.setGenreId(request.getGenreId());
                b.setIsbn(request.getIsbn());
                final var book = bookRepository.save(b);
                return findById(book.getId());
            })
            .orElseThrow(RepositoryDataNotfoundException::new);
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void createSalesUnitPrice(@NonNull Long bookId, @NonNull BookSalesUnitPriceCreateRequest request) {
        final var book = bookRepository.findByIdWithWriteLock(bookId)
            .orElseThrow(RepositoryDataNotfoundException::new);
        final var followingHistories = bookSalesUnitPriceHistoryRepository
            .findByBookIdAndEffectiveFromGreaterThanEqualOrderByEffectiveFrom(book.getId(), request.getEffectiveFrom());
        if (!followingHistories.isEmpty() && followingHistories.getFirst().getEffectiveFrom().equals(request.getEffectiveFrom())) {
            throw new UniqueConstraintValidationException(
                "book_sales_unit_price_history",
                "book_id,effective_from",
                book.getId() + "," + request.getEffectiveFrom()
            );
        }

        final var previousHistory = bookSalesUnitPriceHistoryRepository.findPreviousHistory(book.getId(), request.getEffectiveFrom())
            .orElseThrow(RepositoryDataNotfoundException::new);
        previousHistory.setEffectiveTo(request.getEffectiveFrom().minusDays(1));
        final var effectiveTo = followingHistories.isEmpty() ? null : followingHistories.getFirst().getEffectiveFrom().minusDays(1);
        insertSalesUnitPriceHistory(book.getId(), request.getSalesUnitPrice(), request.getEffectiveFrom(), effectiveTo);
    }

    @RetryableOnLockFailure
    @Transactional
    @Override
    public void delete(@NonNull Long id) {
        final var book = bookRepository.findByIdWithWriteLock(id)
            .orElseThrow(RepositoryDataNotfoundException::new);

        bookRepository.deleteById(book.getId());
    }

    private void insertSalesUnitPriceHistory(Long bookId, Integer salesUnitPrice, LocalDate effectiveFrom, LocalDate effectiveTo) {
        bookSalesUnitPriceHistoryRepository.insertWithId(
            bookSalesUnitPriceHistoryRepository.nextId(),
            bookId,
            salesUnitPrice,
            effectiveFrom,
            effectiveTo,
            LocalDateTime.now()
        );
    }
}
