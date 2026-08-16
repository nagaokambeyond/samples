package app

import (
	"database/sql"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"codex-poting/golang/internal/service"
)

func TestSalesPriceAcceptance(t *testing.T) {
	const (
		futureDate1 = "2026-09-05"
		futureDate2 = "2026-09-25"
	)

	t.Run("PRICE-001 add sales price", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000701")
		w := addSalesPrice(t, env, token, book.ID, 1500, futureDate1)
		if w.Code != http.StatusOK || w.Body.Len() != 0 {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
		assertPriceHistory(t, env.db, book.ID, 1200, "2026-01-01", nullableDate("2026-09-04"))
		assertPriceHistory(t, env.db, book.ID, 1500, futureDate1, sql.NullString{})
	})

	t.Run("PRICE-002 invalid request", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := addSalesPrice(t, env, acceptanceToken(t, env.router), 1, 0, "2026-08-16")
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || !acceptanceHasField(problem, "salesUnitPrice") || !acceptanceHasField(problem, "effectiveFrom") {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("PRICE-003 token required", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		if w := addSalesPrice(t, env, "", 1, 1500, futureDate1); w.Code != http.StatusUnauthorized {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("PRICE-004 missing book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := addSalesPrice(t, env, acceptanceToken(t, env.router), 999, 1500, futureDate1)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusNotFound || problem.Title != "該当データなし" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("PRICE-005 duplicate effective from", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000705")
		if w := addSalesPrice(t, env, token, book.ID, 1500, futureDate1); w.Code != http.StatusOK {
			t.Fatalf("first status=%d", w.Code)
		}
		w := addSalesPrice(t, env, token, book.ID, 1800, futureDate1)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || problem.Title != "データバリデーション" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("PRICE-006 insert between histories", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000706")
		if w := addSalesPrice(t, env, token, book.ID, 1800, futureDate2); w.Code != http.StatusOK {
			t.Fatalf("future2 status=%d body=%s", w.Code, w.Body.String())
		}
		if w := addSalesPrice(t, env, token, book.ID, 1500, futureDate1); w.Code != http.StatusOK {
			t.Fatalf("future1 status=%d body=%s", w.Code, w.Body.String())
		}
		assertPriceHistory(t, env.db, book.ID, 1200, "2026-01-01", nullableDate("2026-09-04"))
		assertPriceHistory(t, env.db, book.ID, 1500, futureDate1, nullableDate("2026-09-24"))
		assertPriceHistory(t, env.db, book.ID, 1800, futureDate2, sql.NullString{})
	})

	t.Run("PRICE-007 price boundaries", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book1 := acceptanceCreateBook(t, env, token, "9784000000707")
		book2 := acceptanceCreateBook(t, env, token, "9784000000708")
		if addSalesPrice(t, env, token, book1.ID, 1, futureDate1).Code != http.StatusOK || addSalesPrice(t, env, token, book2.ID, 10000, futureDate1).Code != http.StatusOK {
			t.Fatal("valid boundary was rejected")
		}
		if addSalesPrice(t, env, token, book1.ID, 0, futureDate2).Code != http.StatusBadRequest || addSalesPrice(t, env, token, book2.ID, 10001, futureDate2).Code != http.StatusBadRequest {
			t.Fatal("invalid boundary was accepted")
		}
	})

	t.Run("PRICE-008 today rejected tomorrow accepted", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book1 := acceptanceCreateBook(t, env, token, "9784000000709")
		book2 := acceptanceCreateBook(t, env, token, "9784000000710")
		if w := addSalesPrice(t, env, token, book1.ID, 1500, "2026-08-16"); w.Code != http.StatusBadRequest {
			t.Fatalf("today status=%d", w.Code)
		}
		if w := addSalesPrice(t, env, token, book2.ID, 1500, "2026-08-17"); w.Code != http.StatusOK {
			t.Fatalf("tomorrow status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("PRICE-009 continuous periods and current price", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000711")
		_ = addSalesPrice(t, env, token, book.ID, 1500, futureDate1)
		_ = addSalesPrice(t, env, token, book.ID, 1800, futureDate2)
		checks := []struct {
			date  time.Time
			price int64
		}{
			{time.Date(2026, 9, 4, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60)), 1200},
			{time.Date(2026, 9, 5, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60)), 1500},
			{time.Date(2026, 9, 24, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60)), 1500},
			{time.Date(2026, 9, 25, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60)), 1800},
		}
		for _, check := range checks {
			env.clock.Set(check.date)
			w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/"+formatID(book.ID), "", nil)
			got := acceptanceDecode[service.BookResponse](t, w)
			if w.Code != http.StatusOK || got.SalesUnitPrice != check.price {
				t.Fatalf("date=%s status=%d price=%d", check.date, w.Code, got.SalesUnitPrice)
			}
		}
	})

	t.Run("PRICE-010 insert failure rolls back prior history", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000712")
		_, err := env.db.Exec(`CREATE TRIGGER fail_new_price BEFORE INSERT ON book_sales_unit_price_history WHEN NEW.effective_from = '2026-09-05' BEGIN SELECT RAISE(FAIL, 'injected price failure'); END`)
		if err != nil {
			t.Fatal(err)
		}
		w := addSalesPrice(t, env, token, book.ID, 1500, futureDate1)
		if w.Code < 500 {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
		assertPriceHistory(t, env.db, book.ID, 1200, "2026-01-01", sql.NullString{})
		if acceptanceCount(t, env.db, "SELECT count(*) FROM book_sales_unit_price_history WHERE book_id = ?", book.ID) != 1 {
			t.Fatal("partial price history remained")
		}
	})
}

func addSalesPrice(t *testing.T, env *acceptanceEnv, token string, bookID, price int64, effectiveFrom string) *httptest.ResponseRecorder {
	t.Helper()
	return acceptanceRequest(t, env.router, http.MethodPost, "/api/books/"+formatID(bookID)+"/sales-unit-prices", token, map[string]interface{}{
		"salesUnitPrice": price, "effectiveFrom": effectiveFrom,
	})
}

func assertPriceHistory(t *testing.T, sqlDB *sql.DB, bookID, price int64, effectiveFrom string, effectiveTo sql.NullString) {
	t.Helper()
	var gotPrice int64
	var gotTo sql.NullString
	if err := sqlDB.QueryRow("SELECT sales_unit_price, effective_to FROM book_sales_unit_price_history WHERE book_id = ? AND effective_from = ?", bookID, effectiveFrom).Scan(&gotPrice, &gotTo); err != nil {
		t.Fatal(err)
	}
	if gotPrice != price || gotTo != effectiveTo {
		t.Fatalf("history book=%d from=%s price=%d to=%+v", bookID, effectiveFrom, gotPrice, gotTo)
	}
}

func nullableDate(value string) sql.NullString {
	return sql.NullString{String: value, Valid: true}
}
