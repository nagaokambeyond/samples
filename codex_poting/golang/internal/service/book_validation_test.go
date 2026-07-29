package service

import (
	"context"
	"strings"
	"testing"
	"time"

	"codex-poting/golang/internal/db"
	"codex-poting/golang/internal/problem"
)

func TestValidateBookSearchCorrelation(t *testing.T) {
	tests := []struct {
		name      string
		from      string
		to        string
		wantTitle string
	}{
		{name: "no dates", from: "", to: "", wantTitle: ""},
		{name: "valid range", from: "2020-01-01", to: "2020-01-02", wantTitle: ""},
		{name: "missing to", from: "2020-01-01", to: "", wantTitle: "相関バリデーション"},
		{name: "missing from", from: "", to: "2020-01-02", wantTitle: "相関バリデーション"},
		{name: "from after to", from: "2020-01-02", to: "2020-01-01", wantTitle: "相関バリデーション"},
		{name: "invalid from date", from: "invalid", to: "2020-01-01", wantTitle: "リクエストエラー"},
		{name: "invalid to date", from: "2020-01-01", to: "invalid", wantTitle: "リクエストエラー"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := validateBookSearchCorrelation(tt.from, tt.to)
			if tt.wantTitle == "" {
				if err != nil {
					t.Fatalf("unexpected error: %v", err)
				}
				return
			}
			if err == nil {
				t.Fatal("expected error, got nil")
			}
			got, ok := problem.As(err)
			if !ok {
				t.Fatalf("expected problem error, got %T", err)
			}
			if got.Title != tt.wantTitle {
				t.Fatalf("title = %q, want %q", got.Title, tt.wantTitle)
			}
		})
	}
}

func TestValidateBookCreateRequest(t *testing.T) {
	fields := validateBookCreate(BookCreateRequest{})

	for _, field := range []string{"title", "releaseDate", "publisherId", "genreId", "isbn", "salesUnitPrice"} {
		if !hasFieldError(fields, field) {
			t.Fatalf("expected field %q in errors: %+v", field, fields)
		}
	}
}

func TestValidateBookUpdateRequest(t *testing.T) {
	fields := validateBookUpdate(BookUpdateRequest{})

	for _, field := range []string{"id", "title", "releaseDate", "publisherId", "genreId", "isbn", "version"} {
		if !hasFieldError(fields, field) {
			t.Fatalf("expected field %q in errors: %+v", field, fields)
		}
	}
}

func TestValidateSalesPriceRequest(t *testing.T) {
	price := int64(0)
	past := time.Now().AddDate(0, 0, -1).Format("2006-01-02")

	fields := validateSalesPrice(SalesUnitPriceCreateRequest{SalesUnitPrice: &price, EffectiveFrom: &past})

	for _, field := range []string{"salesUnitPrice", "effectiveFrom"} {
		if !hasFieldError(fields, field) {
			t.Fatalf("expected field %q in errors: %+v", field, fields)
		}
	}
}

func TestValidateBookRefs(t *testing.T) {
	svc, cleanup := testService(t)
	defer cleanup()

	tests := []struct {
		name        string
		publisherID int64
		genreID     int64
		wantDetail  string
	}{
		{name: "valid refs", publisherID: 1, genreID: 5, wantDetail: ""},
		{name: "publisher not found", publisherID: 999, genreID: 5, wantDetail: "publisher(id=999)"},
		{name: "genre not found", publisherID: 1, genreID: 999, wantDetail: "book_genre(id=999)"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := svc.validateBookRefs(context.Background(), svc.q, tt.publisherID, tt.genreID)
			if tt.wantDetail == "" {
				if err != nil {
					t.Fatalf("unexpected error: %v", err)
				}
				return
			}
			if err == nil {
				t.Fatal("expected error, got nil")
			}
			got, ok := problem.As(err)
			if !ok {
				t.Fatalf("expected problem error, got %T", err)
			}
			if got.Title != "データバリデーション" || !strings.Contains(got.Detail.Detail, tt.wantDetail) {
				t.Fatalf("unexpected problem: %+v", got)
			}
		})
	}
}

func hasFieldError(fields []problem.FieldError, field string) bool {
	for _, item := range fields {
		if item.Field == field {
			return true
		}
	}
	return false
}

func testService(t *testing.T) (*Service, func()) {
	t.Helper()
	sqlDB, err := db.OpenMemory()
	if err != nil {
		t.Fatal(err)
	}
	return New(sqlDB, 10, false), func() { sqlDB.Close() }
}
