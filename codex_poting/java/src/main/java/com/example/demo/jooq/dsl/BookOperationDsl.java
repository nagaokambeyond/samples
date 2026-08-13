package com.example.demo.jooq.dsl;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.jooq.entity.BookSalesUnitPriceHistoryRow;
import com.example.demo.jooq.entity.BookWithStockRow;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.exception.DataAccessException;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.example.demo.jooq.generated.Tables.BOOK;
import static com.example.demo.jooq.generated.Tables.BOOK_GENRE;
import static com.example.demo.jooq.generated.Tables.BOOK_SALES_UNIT_PRICE_HISTORY;
import static com.example.demo.jooq.generated.Tables.BOOK_STOCK;
import static com.example.demo.jooq.generated.Tables.PUBLISHER;
import static com.example.demo.jooq.generated.Tables.STORE;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.lower;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.noCondition;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class BookOperationDsl {
    private final DSLContext dsl;

    public org.jooq.Record2<Long, Long> selectBookForUpdate(Long id) {
        return executeWithLockException(() -> dsl.select(BOOK.ID, BOOK.VERSION)
            .from(BOOK)
            .where(BOOK.ID.eq(id))
            .forUpdate()
            .noWait()
            .fetchOne());
    }

    public List<BookWithStockRow> selectByIdWithPublisherName(Long id) {
        return dsl.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.AUTHOR,
                BOOK.RELEASE_DATE,
                BOOK.PUBLISHER_ID,
                PUBLISHER.PUBLISHER_NAME,
                BOOK.GENRE_ID,
                BOOK_GENRE.GENRE_NAME,
                BOOK.ISBN,
                BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE,
                BOOK.UPDATE_AT,
                BOOK.VERSION,
                BOOK_STOCK.ID,
                BOOK_STOCK.BOOK_STOCK_STORE_ID,
                STORE.STORE_NAME,
                BOOK_STOCK.BOOK_STOCK_QUANTITY
            )
            .from(BOOK)
            .join(PUBLISHER).on(BOOK.PUBLISHER_ID.eq(PUBLISHER.ID))
            .join(BOOK_GENRE).on(BOOK.GENRE_ID.eq(BOOK_GENRE.ID))
            .join(BOOK_SALES_UNIT_PRICE_HISTORY)
            .on(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(BOOK.ID)
                .and(currentSalesUnitPriceCondition()))
            .leftJoin(BOOK_STOCK).on(BOOK.ID.eq(BOOK_STOCK.BOOK_STOCK_BOOK_ID))
            .leftJoin(STORE).on(BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(STORE.ID))
            .where(BOOK.ID.eq(id))
            .orderBy(BOOK.ID, BOOK_STOCK.ID)
            .fetch(row -> new BookWithStockRow(
                row.get(BOOK.ID),
                row.get(BOOK.TITLE),
                row.get(BOOK.AUTHOR),
                row.get(BOOK.RELEASE_DATE),
                row.get(BOOK.PUBLISHER_ID),
                row.get(PUBLISHER.PUBLISHER_NAME),
                row.get(BOOK.GENRE_ID),
                row.get(BOOK_GENRE.GENRE_NAME),
                row.get(BOOK.ISBN),
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE),
                row.get(BOOK.UPDATE_AT),
                row.get(BOOK.VERSION),
                row.get(BOOK_STOCK.ID),
                row.get(BOOK_STOCK.BOOK_STOCK_STORE_ID),
                row.get(STORE.STORE_NAME),
                row.get(BOOK_STOCK.BOOK_STOCK_QUANTITY)
            ));
    }

    public List<BookWithStockRow> selectByTitleOrAuthorStartingWithIgnoreCase(Condition condition, int size, long offset) {
        final var pagedBooks = dsl.select(
                BOOK.ID,
                BOOK.TITLE,
                BOOK.AUTHOR,
                BOOK.RELEASE_DATE,
                BOOK.PUBLISHER_ID,
                BOOK.GENRE_ID,
                BOOK.ISBN,
                BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE,
                BOOK.UPDATE_AT,
                BOOK.VERSION
            )
            .from(BOOK)
            .join(BOOK_SALES_UNIT_PRICE_HISTORY)
            .on(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(BOOK.ID)
                .and(currentSalesUnitPriceCondition()))
            .where(condition)
            .orderBy(BOOK.ID)
            .limit(size)
            .offset(offset)
            .asTable("paged_books");

        final Field<Long> id = Objects.requireNonNull(pagedBooks.field(BOOK.ID));
        final Field<String> title = Objects.requireNonNull(pagedBooks.field(BOOK.TITLE));
        final Field<String> author = Objects.requireNonNull(pagedBooks.field(BOOK.AUTHOR));
        final Field<LocalDate> releaseDate = Objects.requireNonNull(pagedBooks.field(BOOK.RELEASE_DATE));
        final Field<Long> publisherId = Objects.requireNonNull(pagedBooks.field(BOOK.PUBLISHER_ID));
        final Field<Long> genreId = Objects.requireNonNull(pagedBooks.field(BOOK.GENRE_ID));
        final Field<String> isbn = Objects.requireNonNull(pagedBooks.field(BOOK.ISBN));
        final Field<Integer> salesUnitPrice = Objects.requireNonNull(pagedBooks.field(BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE));
        final Field<LocalDateTime> updateAt = Objects.requireNonNull(pagedBooks.field(BOOK.UPDATE_AT));
        final Field<Long> version = Objects.requireNonNull(pagedBooks.field(BOOK.VERSION));

        return dsl.select(
                id,
                title,
                author,
                releaseDate,
                publisherId,
                PUBLISHER.PUBLISHER_NAME,
                genreId,
                BOOK_GENRE.GENRE_NAME,
                isbn,
                salesUnitPrice,
                updateAt,
                version,
                BOOK_STOCK.ID,
                BOOK_STOCK.BOOK_STOCK_STORE_ID,
                STORE.STORE_NAME,
                BOOK_STOCK.BOOK_STOCK_QUANTITY
            )
            .from(pagedBooks)
            .join(PUBLISHER).on(publisherId.eq(PUBLISHER.ID))
            .join(BOOK_GENRE).on(genreId.eq(BOOK_GENRE.ID))
            .leftJoin(BOOK_STOCK).on(id.eq(BOOK_STOCK.BOOK_STOCK_BOOK_ID))
            .leftJoin(STORE).on(BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(STORE.ID))
            .orderBy(id, BOOK_STOCK.ID)
            .fetch(row -> new BookWithStockRow(
                row.get(id),
                row.get(title),
                row.get(author),
                row.get(releaseDate),
                row.get(publisherId),
                row.get(PUBLISHER.PUBLISHER_NAME),
                row.get(genreId),
                row.get(BOOK_GENRE.GENRE_NAME),
                row.get(isbn),
                row.get(salesUnitPrice),
                row.get(updateAt),
                row.get(version),
                row.get(BOOK_STOCK.ID),
                row.get(BOOK_STOCK.BOOK_STOCK_STORE_ID),
                row.get(STORE.STORE_NAME),
                row.get(BOOK_STOCK.BOOK_STOCK_QUANTITY)
            ));
    }

    public Condition searchCondition(String keyword, LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        final var conditions = new ArrayList<Condition>();
        if (keyword != null && !keyword.isBlank()) {
            final var pattern = keyword.trim().toLowerCase() + "%";
            conditions.add(lower(BOOK.TITLE).like(pattern).or(lower(BOOK.AUTHOR).like(pattern)));
        }
        if (releaseDateFrom != null) {
            conditions.add(BOOK.RELEASE_DATE.ge(releaseDateFrom));
        }
        if (releaseDateTo != null) {
            conditions.add(BOOK.RELEASE_DATE.le(releaseDateTo));
        }
        return conditions.stream().reduce(noCondition(), Condition::and);
    }

    public int totalElements(Condition condition) {
        return dsl.fetchCount(dsl.selectOne()
            .from(BOOK)
            .join(BOOK_SALES_UNIT_PRICE_HISTORY)
            .on(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(BOOK.ID)
                .and(currentSalesUnitPriceCondition()))
            .where(condition));
    }

    public Long insert(@NonNull BookCreateRequest request){
        final var now = LocalDateTime.now();
        return dsl.insertInto(BOOK)
            .set(BOOK.TITLE, request.getTitle())
            .set(BOOK.AUTHOR, request.getAuthor())
            .set(BOOK.RELEASE_DATE, request.getReleaseDate())
            .set(BOOK.PUBLISHER_ID, request.getPublisherId())
            .set(BOOK.GENRE_ID, request.getGenreId())
            .set(BOOK.ISBN, request.getIsbn())
            .set(BOOK.CREATE_AT, now)
            .set(BOOK.UPDATE_AT, now)
            .set(BOOK.VERSION, 1L)
            .returningResult(BOOK.ID)
            .fetchOne(BOOK.ID);
    }

    public void insertSalesUnitPriceHistory(Long bookId, Integer salesUnitPrice, LocalDate effectiveFrom, LocalDate effectiveTo, LocalDateTime now) {
        dsl.insertInto(BOOK_SALES_UNIT_PRICE_HISTORY)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.ID, selectNextSalesUnitPriceHistoryId())
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID, bookId)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE, salesUnitPrice)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM, effectiveFrom)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO, effectiveTo)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.CREATE_AT, now)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.UPDATE_AT, now)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.VERSION, 1L)
            .execute();
    }

    private Long selectNextSalesUnitPriceHistoryId() {
        return dsl.select(coalesce(max(BOOK_SALES_UNIT_PRICE_HISTORY.ID), 0L).add(1L))
            .from(BOOK_SALES_UNIT_PRICE_HISTORY)
            .fetchOne(0, Long.class);
    }

    public List<BookSalesUnitPriceHistoryRow> selectFollowingSalesUnitPriceHistories(Long bookId, LocalDate effectiveFrom) {
        return dsl.select(BOOK_SALES_UNIT_PRICE_HISTORY.ID, BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM, BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
            .from(BOOK_SALES_UNIT_PRICE_HISTORY)
            .where(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(bookId))
            .and(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.ge(effectiveFrom))
            .orderBy(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM)
            .fetch(row -> new BookSalesUnitPriceHistoryRow(
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.ID),
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM),
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
            ));
    }

    public BookSalesUnitPriceHistoryRow selectPreviousSalesUnitPriceHistory(Long bookId, LocalDate effectiveFrom) {
        return dsl.select(BOOK_SALES_UNIT_PRICE_HISTORY.ID, BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM, BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
            .from(BOOK_SALES_UNIT_PRICE_HISTORY)
            .where(BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(bookId))
            .and(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.lt(effectiveFrom))
            .orderBy(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.desc())
            .limit(1)
            .fetchOne(row -> new BookSalesUnitPriceHistoryRow(
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.ID),
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM),
                row.get(BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
            ));
    }

    public void updateSalesUnitPriceHistoryEffectiveTo(BookSalesUnitPriceHistoryRow history, LocalDate effectiveTo) {
        dsl.update(BOOK_SALES_UNIT_PRICE_HISTORY)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO, effectiveTo)
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.UPDATE_AT, LocalDateTime.now())
            .set(BOOK_SALES_UNIT_PRICE_HISTORY.VERSION, history.getVersion() + 1)
            .where(BOOK_SALES_UNIT_PRICE_HISTORY.ID.eq(history.getId()))
            .execute();
    }

    public void update(@NonNull BookUpdateRequest request, Long currentVersion){
        dsl.update(BOOK)
            .set(BOOK.TITLE, request.getTitle())
            .set(BOOK.AUTHOR, request.getAuthor())
            .set(BOOK.RELEASE_DATE, request.getReleaseDate())
            .set(BOOK.PUBLISHER_ID, request.getPublisherId())
            .set(BOOK.GENRE_ID, request.getGenreId())
            .set(BOOK.ISBN, request.getIsbn())
            .set(BOOK.UPDATE_AT, LocalDateTime.now())
            .set(BOOK.VERSION, currentVersion + 1)
            .where(BOOK.ID.eq(request.getId()))
            .execute();
    }

    public void delete(@NonNull Long id){
        dsl.deleteFrom(BOOK)
            .where(BOOK.ID.eq(id))
            .execute();
    }

    private <T> T executeWithLockException(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            throw new PessimisticLockingFailureException("jOOQ write lock could not be acquired", ex);
        }
    }

    private Condition currentSalesUnitPriceCondition() {
        final var today = LocalDate.now();
        return BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.le(today)
            .and(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO.isNull()
                .or(BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO.ge(today)));
    }
}
