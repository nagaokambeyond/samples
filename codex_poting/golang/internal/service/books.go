package service

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"math"
	"strings"

	"codex-poting/golang/internal/dbsqlc"
	"codex-poting/golang/internal/problem"
)

func (s *Service) GetBook(ctx context.Context, id int64) (BookResponse, error) {
	return s.getBookWith(ctx, s.q, id)
}

func (s *Service) SearchBooks(ctx context.Context, keyword, from, to string, page int64) (BookPageResponse, error) {
	if page < 0 {
		return BookPageResponse{}, problem.Request([]problem.FieldError{{Field: "page", Message: "0 以上の値にしてください"}}, "page: 0 以上の値にしてください")
	}
	if err := validateBookSearchCorrelation(from, to); err != nil {
		return BookPageResponse{}, err
	}
	keyword = strings.TrimSpace(keyword)
	params := dbsqlc.SearchBookBasesParams{
		Today: today(), Keyword: keyword, ReleaseDateFrom: from, ReleaseDateTo: to,
		LimitRows: s.pageSize, OffsetRows: page * s.pageSize,
	}
	rows, err := s.q.SearchBookBases(ctx, params)
	if err != nil {
		return BookPageResponse{}, err
	}
	total, err := s.q.CountBookSearch(ctx, dbsqlc.CountBookSearchParams{Today: today(), Keyword: keyword, ReleaseDateFrom: from, ReleaseDateTo: to})
	if err != nil {
		return BookPageResponse{}, err
	}
	content := make([]BookResponse, 0, len(rows))
	for _, row := range rows {
		book, err := s.bookFromBase(ctx, s.q, baseFromSearch(row))
		if err != nil {
			return BookPageResponse{}, err
		}
		content = append(content, book)
	}
	totalPages := int64(0)
	if total > 0 {
		totalPages = int64(math.Ceil(float64(total) / float64(s.pageSize)))
	}
	return BookPageResponse{Content: content, Page: page, Size: s.pageSize, TotalElements: total, TotalPages: totalPages}, nil
}

func (s *Service) CreateBook(ctx context.Context, req BookCreateRequest) (BookResponse, error) {
	if fields := validateBookCreate(req); len(fields) > 0 {
		return BookResponse{}, problem.Validation(fields)
	}
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return BookResponse{}, err
	}
	defer tx.Rollback()
	q := s.txQueries(tx)
	if err := s.validateBookRefs(ctx, q, *req.PublisherID, *req.GenreID); err != nil {
		return BookResponse{}, err
	}
	if id, err := q.FindBookByISBN(ctx, *req.Isbn); err == nil && id > 0 {
		return BookResponse{}, problem.DataValidation(fmt.Sprintf("一意制約に違反しています: book(isbn=%s)", *req.Isbn))
	} else if err != nil && !errors.Is(err, sql.ErrNoRows) {
		return BookResponse{}, err
	}
	now := nowString()
	id, err := q.CreateBook(ctx, dbsqlc.CreateBookParams{
		Title: *req.Title, Author: nullableString(req.Author), ReleaseDate: *req.ReleaseDate,
		PublisherID: *req.PublisherID, GenreID: *req.GenreID, Isbn: *req.Isbn, Now: now,
	})
	if err != nil {
		return BookResponse{}, err
	}
	if _, err := q.CreateSalesUnitPriceHistory(ctx, dbsqlc.CreateSalesUnitPriceHistoryParams{
		BookID: id, SalesUnitPrice: *req.SalesUnitPrice, EffectiveFrom: *req.ReleaseDate, Now: now,
	}); err != nil {
		return BookResponse{}, err
	}
	if err := tx.Commit(); err != nil {
		return BookResponse{}, err
	}
	return s.GetBook(ctx, id)
}

func (s *Service) UpdateBook(ctx context.Context, req BookUpdateRequest) (BookResponse, error) {
	if fields := validateBookUpdate(req); len(fields) > 0 {
		return BookResponse{}, problem.Validation(fields)
	}
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return BookResponse{}, err
	}
	defer tx.Rollback()
	q := s.txQueries(tx)
	if err := s.validateBookRefs(ctx, q, *req.PublisherID, *req.GenreID); err != nil {
		return BookResponse{}, err
	}
	exists, err := q.ExistsBookID(ctx, *req.ID)
	if err != nil {
		return BookResponse{}, err
	}
	if !exists {
		return BookResponse{}, problem.NotFound()
	}
	if id, err := q.FindBookByISBN(ctx, *req.Isbn); err == nil && id != *req.ID {
		return BookResponse{}, problem.DataValidation(fmt.Sprintf("一意制約に違反しています: book(isbn=%s)", *req.Isbn))
	} else if err != nil && !errors.Is(err, sql.ErrNoRows) {
		return BookResponse{}, err
	}
	rows, err := q.UpdateBook(ctx, dbsqlc.UpdateBookParams{
		ID: *req.ID, Title: *req.Title, Author: nullableString(req.Author), ReleaseDate: *req.ReleaseDate,
		PublisherID: *req.PublisherID, GenreID: *req.GenreID, Isbn: *req.Isbn, Version: *req.Version, Now: nowString(),
	})
	if err != nil {
		return BookResponse{}, err
	}
	if rows == 0 {
		return BookResponse{}, problem.Conflict()
	}
	if err := tx.Commit(); err != nil {
		return BookResponse{}, err
	}
	return s.GetBook(ctx, *req.ID)
}

func (s *Service) DeleteBook(ctx context.Context, id int64) error {
	rows, err := s.q.DeleteBook(ctx, id)
	if err != nil {
		return problem.DataValidation(err.Error())
	}
	if rows == 0 {
		return problem.NotFound()
	}
	return nil
}

func (s *Service) CreateSalesUnitPrice(ctx context.Context, bookID int64, req SalesUnitPriceCreateRequest) error {
	if fields := validateSalesPrice(req); len(fields) > 0 {
		return problem.Validation(fields)
	}
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()
	q := s.txQueries(tx)
	exists, err := q.ExistsBookID(ctx, bookID)
	if err != nil {
		return err
	}
	if !exists {
		return problem.NotFound()
	}
	next, nextErr := q.GetNextSalesUnitPriceHistory(ctx, dbsqlc.GetNextSalesUnitPriceHistoryParams{BookID: bookID, EffectiveFrom: *req.EffectiveFrom})
	if nextErr == nil && next.EffectiveFrom == *req.EffectiveFrom {
		return problem.DataValidation(fmt.Sprintf("一意制約に違反しています: book_sales_unit_price_history(book_id,effective_from=%d,%s)", bookID, *req.EffectiveFrom))
	}
	if nextErr != nil && !errors.Is(nextErr, sql.ErrNoRows) {
		return nextErr
	}
	prev, err := q.GetPrevSalesUnitPriceHistory(ctx, dbsqlc.GetPrevSalesUnitPriceHistoryParams{BookID: bookID, EffectiveFrom: *req.EffectiveFrom})
	if errors.Is(err, sql.ErrNoRows) {
		return problem.NotFound()
	}
	if err != nil {
		return err
	}
	eff, _ := parseDate(*req.EffectiveFrom)
	prevTo := eff.AddDate(0, 0, -1).Format("2006-01-02")
	now := nowString()
	if _, err := q.UpdateSalesUnitPriceHistoryEffectiveTo(ctx, dbsqlc.UpdateSalesUnitPriceHistoryEffectiveToParams{ID: prev.ID, EffectiveTo: sql.NullString{String: prevTo, Valid: true}, Now: now}); err != nil {
		return err
	}
	var nextTo sql.NullString
	if nextErr == nil {
		nextFrom, _ := parseDate(next.EffectiveFrom)
		nextTo = sql.NullString{String: nextFrom.AddDate(0, 0, -1).Format("2006-01-02"), Valid: true}
	}
	_, err = q.CreateSalesUnitPriceHistory(ctx, dbsqlc.CreateSalesUnitPriceHistoryParams{
		BookID: bookID, SalesUnitPrice: *req.SalesUnitPrice, EffectiveFrom: *req.EffectiveFrom, EffectiveTo: nextTo, Now: now,
	})
	if err != nil {
		return problem.DataValidation(err.Error())
	}
	return tx.Commit()
}

func (s *Service) getBookWith(ctx context.Context, q *dbsqlc.Queries, id int64) (BookResponse, error) {
	base, err := q.GetBookBase(ctx, dbsqlc.GetBookBaseParams{ID: id, Today: today()})
	if errors.Is(err, sql.ErrNoRows) {
		return BookResponse{}, problem.NotFound()
	}
	if err != nil {
		return BookResponse{}, err
	}
	return s.bookFromBase(ctx, q, baseFromGet(base))
}

type bookBase struct {
	ID             int64
	Title          string
	Author         sql.NullString
	ReleaseDate    string
	PublisherID    int64
	PublisherName  string
	GenreID        int64
	GenreName      string
	Isbn           string
	SalesUnitPrice int64
	UpdateAt       string
	Version        int64
}

func baseFromGet(row dbsqlc.GetBookBaseRow) bookBase {
	return bookBase{
		ID: row.ID, Title: row.Title, Author: row.Author, ReleaseDate: row.ReleaseDate, PublisherID: row.PublisherID,
		PublisherName: row.PublisherName, GenreID: row.GenreID, GenreName: row.GenreName, Isbn: row.Isbn,
		SalesUnitPrice: row.SalesUnitPrice, UpdateAt: row.UpdateAt, Version: row.Version,
	}
}

func baseFromSearch(row dbsqlc.SearchBookBasesRow) bookBase {
	return bookBase{
		ID: row.ID, Title: row.Title, Author: row.Author, ReleaseDate: row.ReleaseDate, PublisherID: row.PublisherID,
		PublisherName: row.PublisherName, GenreID: row.GenreID, GenreName: row.GenreName, Isbn: row.Isbn,
		SalesUnitPrice: row.SalesUnitPrice, UpdateAt: row.UpdateAt, Version: row.Version,
	}
}

func (s *Service) bookFromBase(ctx context.Context, q *dbsqlc.Queries, row bookBase) (BookResponse, error) {
	stocks, err := q.ListBookStocks(ctx, row.ID)
	if err != nil {
		return BookResponse{}, err
	}
	outStocks := make([]BookStockResponse, 0, len(stocks))
	for _, st := range stocks {
		outStocks = append(outStocks, BookStockResponse{
			ID: st.ID, BookStockStoreID: st.BookStockStoreID, StoreName: st.StoreName, BookStockQuantity: st.BookStockQuantity,
		})
	}
	var author *string
	if row.Author.Valid {
		author = &row.Author.String
	}
	return BookResponse{
		ID: row.ID, Title: row.Title, Author: author, ReleaseDate: row.ReleaseDate,
		PublisherID: row.PublisherID, PublisherName: row.PublisherName, GenreID: row.GenreID, GenreName: row.GenreName,
		Isbn: row.Isbn, SalesUnitPrice: row.SalesUnitPrice, UpdateAt: row.UpdateAt, Version: row.Version, BookStockList: outStocks,
	}, nil
}
