package app

import (
	"bytes"
	"encoding/json"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"codex-poting/golang/internal/applog"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
)

func TestBookSearchAndGet(t *testing.T) {
	router := testRouter(t)

	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/api/books/search?page=0", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	var page struct {
		TotalElements int `json:"totalElements"`
		TotalPages    int `json:"totalPages"`
		Content       []struct {
			ID int64 `json:"id"`
		} `json:"content"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &page); err != nil {
		t.Fatal(err)
	}
	if page.TotalElements != 21 || page.TotalPages != 3 || len(page.Content) != 10 {
		t.Fatalf("unexpected page: %+v", page)
	}

	w = httptest.NewRecorder()
	req = httptest.NewRequest(http.MethodGet, "/api/books/1", nil)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	var book struct {
		ID             int64  `json:"id"`
		Title          string `json:"title"`
		SalesUnitPrice int64  `json:"salesUnitPrice"`
		BookStockList  []any  `json:"bookStockList"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &book); err != nil {
		t.Fatal(err)
	}
	if book.ID != 1 || book.Title != "Spring入門" || book.SalesUnitPrice != 1200 || len(book.BookStockList) != 3 {
		t.Fatalf("unexpected book: %+v", book)
	}
}

func TestLoginAndCreateBook(t *testing.T) {
	router := testRouter(t)
	token := login(t, router)

	body := []byte(`{
		"title":"ISBN登録",
		"author":"Jiro",
		"releaseDate":"2026-01-01",
		"publisherId":1,
		"genreId":5,
		"isbn":"9784000000501",
		"salesUnitPrice":1200
	}`)
	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodPost, "/api/books/create", bytes.NewReader(body))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	var book struct {
		Title          string `json:"title"`
		Version        int64  `json:"version"`
		BookStockList  []any  `json:"bookStockList"`
		SalesUnitPrice int64  `json:"salesUnitPrice"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &book); err != nil {
		t.Fatal(err)
	}
	if book.Title != "ISBN登録" || book.Version != 1 || book.SalesUnitPrice != 1200 || len(book.BookStockList) != 0 {
		t.Fatalf("unexpected created book: %+v", book)
	}
}

func TestOpenAPIAndSwaggerUI(t *testing.T) {
	router := testRouter(t)

	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/openapi.yaml", nil)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("openapi status = %d, body = %s", w.Code, w.Body.String())
	}
	if !bytes.Contains(w.Body.Bytes(), []byte("openapi: 3.0.3")) {
		t.Fatalf("openapi yaml was not served: %s", w.Body.String())
	}

	w = httptest.NewRecorder()
	req = httptest.NewRequest(http.MethodGet, "/swagger/index.html", nil)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("swagger status = %d, body = %s", w.Code, w.Body.String())
	}

	w = httptest.NewRecorder()
	req = httptest.NewRequest(http.MethodGet, "/scalar", nil)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("scalar status = %d, body = %s", w.Code, w.Body.String())
	}
	if !bytes.Contains(w.Body.Bytes(), []byte("Scalar.createApiReference")) {
		t.Fatalf("scalar html was not served: %s", w.Body.String())
	}
}

func TestAPIBodyLogMasksSensitiveValues(t *testing.T) {
	var logs bytes.Buffer
	restoreLogger := replaceLogger(&logs, "debug")
	t.Cleanup(restoreLogger)

	router := testRouter(t)
	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodPost, "/api/auth/login", bytes.NewReader([]byte(`{"username":"admin","password":"password"}`)))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("login status = %d, body = %s", w.Code, w.Body.String())
	}
	got := logs.String()
	if !strings.Contains(got, "requestBody=") || !strings.Contains(got, "responseBody=") || !strings.Contains(got, "level=DEBUG") {
		t.Fatalf("body log was not written: %s", got)
	}
	if strings.Contains(got, `"password":"password"`) || strings.Contains(got, `\"password\":\"password\"`) {
		t.Fatalf("sensitive values were not masked: %s", got)
	}
	if strings.Contains(got, "accessToken") && !strings.Contains(got, `\"accessToken\":\"***\"`) && !strings.Contains(got, `"accessToken":"***"`) {
		t.Fatalf("sensitive values were not masked: %s", got)
	}
}

func TestSQLLogWritten(t *testing.T) {
	var logs bytes.Buffer
	restoreLogger := replaceLogger(&logs, "debug")
	t.Cleanup(restoreLogger)

	router := testRouter(t)
	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/api/books/search?page=0", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	got := logs.String()
	if !strings.Contains(got, "sql_exec") {
		t.Fatalf("SQL log was not written: %s", got)
	}
	if !strings.Contains(got, "level=DEBUG") || !strings.Contains(got, "SELECT") || !strings.Contains(got, "args=") {
		t.Fatalf("SQL log does not include query and args: %s", got)
	}
}

func TestDebugLogsSuppressedAtInfoLevel(t *testing.T) {
	var logs bytes.Buffer
	restoreLogger := replaceLogger(&logs, "info")
	t.Cleanup(restoreLogger)

	router := testRouter(t)
	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/api/books/search?page=0", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	got := logs.String()
	if strings.Contains(got, "api_body") || strings.Contains(got, "sql_exec") {
		t.Fatalf("debug logs should be suppressed at info level: %s", got)
	}
}

func testRouter(t *testing.T) http.Handler {
	t.Helper()
	sqlDB, err := db.OpenMemory()
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { sqlDB.Close() })
	cfg := config.Load()
	cfg.OpenBDBaseURL = "http://127.0.0.1:1"
	return NewRouter(cfg, sqlDB)
}

func login(t *testing.T, router http.Handler) string {
	t.Helper()
	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodPost, "/api/auth/login", bytes.NewReader([]byte(`{"username":"admin","password":"password"}`)))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Fatalf("login status = %d, body = %s", w.Code, w.Body.String())
	}
	var resp struct {
		AccessToken string `json:"accessToken"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &resp); err != nil {
		t.Fatal(err)
	}
	if resp.AccessToken == "" {
		t.Fatal("empty token")
	}
	return resp.AccessToken
}

func replaceLogger(buf *bytes.Buffer, level string) func() {
	original := slog.Default()
	slog.SetDefault(applog.New(buf, level, "text"))
	return func() { slog.SetDefault(original) }
}
