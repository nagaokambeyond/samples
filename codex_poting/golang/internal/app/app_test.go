package app

import (
	"bytes"
	"encoding/json"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"sync"
	"testing"

	"codex-poting/golang/internal/applog"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
)

type appTestFieldError struct {
	Field string `json:"field"`
}

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

func TestOpenBDAcceptance(t *testing.T) {
	const (
		isbn1 = "9784780802047"
		isbn2 = "9784003101018"
	)
	book1 := `{"summary":{"isbn":"9784780802047","title":"おにぎりレシピ101"},"onix":{"RecordReference":"9784780802047","ProductIdentifier":{"IDValue":"9784780802047"}},"hanmoto":{"datemodified":"2025-12-26 11:32:36"}}`
	book2 := `{"summary":{"isbn":"9784003101018","title":"こころ"},"onix":{"RecordReference":"9784003101018","ProductIdentifier":{"IDValue":"9784003101018"}},"hanmoto":{"datemodified":"2025-01-01 00:00:00"}}`

	type stubResponse struct {
		status int
		body   string
	}
	tests := []struct {
		name           string
		requestPath    string
		stubs          map[string]stubResponse
		wantStatus     int
		wantTitle      string
		wantDetail     string
		wantLen        int
		wantStubISBNs  []string
		wantStubCalled bool
		assertBody     func(t *testing.T, body []map[string]any)
	}{
		{
			name:        "OPENBD-001 single ISBN success",
			requestPath: "/api/books/openbd?isbn=" + isbn1,
			stubs: map[string]stubResponse{
				isbn1: {status: http.StatusOK, body: "[" + book1 + "]"},
			},
			wantStatus:     http.StatusOK,
			wantLen:        1,
			wantStubISBNs:  []string{isbn1},
			wantStubCalled: true,
			assertBody: func(t *testing.T, body []map[string]any) {
				t.Helper()
				summary := body[0]["summary"].(map[string]any)
				onix := body[0]["onix"].(map[string]any)
				productIdentifier := onix["ProductIdentifier"].(map[string]any)
				hanmoto := body[0]["hanmoto"].(map[string]any)
				if summary["isbn"] != isbn1 ||
					summary["title"] != "おにぎりレシピ101" ||
					onix["RecordReference"] != isbn1 ||
					productIdentifier["IDValue"] != isbn1 ||
					hanmoto["datemodified"] != "2025-12-26 11:32:36" {
					t.Fatalf("unexpected body: %+v", body[0])
				}
			},
		},
		{
			name:        "OPENBD-002 comma-separated ISBNs are forwarded",
			requestPath: "/api/books/openbd?isbn=" + isbn1 + "," + isbn2,
			stubs: map[string]stubResponse{
				isbn1 + "," + isbn2: {status: http.StatusOK, body: "[" + book1 + "," + book2 + "]"},
			},
			wantStatus:     http.StatusOK,
			wantLen:        2,
			wantStubISBNs:  []string{isbn1 + "," + isbn2},
			wantStubCalled: true,
		},
		{
			name:        "OPENBD-003 null element returns not found",
			requestPath: "/api/books/openbd?isbn=" + isbn1 + "," + isbn2,
			stubs: map[string]stubResponse{
				isbn1 + "," + isbn2: {status: http.StatusOK, body: "[null," + book2 + "]"},
			},
			wantStatus:     http.StatusNotFound,
			wantTitle:      "OpenBD書誌なし",
			wantStubISBNs:  []string{isbn1 + "," + isbn2},
			wantStubCalled: true,
		},
		{
			name:        "OPENBD-004 null response returns not found",
			requestPath: "/api/books/openbd?isbn=" + isbn1,
			stubs: map[string]stubResponse{
				isbn1: {status: http.StatusOK, body: "null"},
			},
			wantStatus:     http.StatusNotFound,
			wantTitle:      "OpenBD書誌なし",
			wantStubISBNs:  []string{isbn1},
			wantStubCalled: true,
		},
		{
			name:        "OPENBD-005 empty array returns not found",
			requestPath: "/api/books/openbd?isbn=" + isbn1,
			stubs: map[string]stubResponse{
				isbn1: {status: http.StatusOK, body: "[]"},
			},
			wantStatus:     http.StatusNotFound,
			wantTitle:      "OpenBD書誌なし",
			wantStubISBNs:  []string{isbn1},
			wantStubCalled: true,
		},
		{
			name:           "OPENBD-006 missing ISBN query returns bad request",
			requestPath:    "/api/books/openbd",
			wantStatus:     http.StatusBadRequest,
			wantTitle:      "リクエストエラー",
			wantStubCalled: false,
		},
		{
			name:           "OPENBD-007 empty ISBN query returns bad request",
			requestPath:    "/api/books/openbd?isbn=",
			wantStatus:     http.StatusBadRequest,
			wantTitle:      "リクエストエラー",
			wantStubCalled: false,
		},
		{
			name:           "OPENBD-008 invalid ISBN query returns bad request",
			requestPath:    "/api/books/openbd?isbn=" + isbn1 + ",invalid",
			wantStatus:     http.StatusBadRequest,
			wantTitle:      "リクエストエラー",
			wantStubCalled: false,
		},
		{
			name:        "OPENBD-009 upstream failure returns bad gateway",
			requestPath: "/api/books/openbd?isbn=" + isbn1,
			stubs: map[string]stubResponse{
				isbn1: {status: http.StatusInternalServerError, body: `{"error":"upstream failed"}`},
			},
			wantStatus:     http.StatusBadGateway,
			wantTitle:      "外部API呼び出しエラー",
			wantDetail:     "OpenBD APIの呼び出しに失敗しました",
			wantStubISBNs:  []string{isbn1},
			wantStubCalled: true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			var mu sync.Mutex
			var gotStubISBNs []string
			stub := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
				if r.URL.Path != "/v1/get" {
					http.NotFound(w, r)
					return
				}
				isbn := r.URL.Query().Get("isbn")
				mu.Lock()
				gotStubISBNs = append(gotStubISBNs, isbn)
				mu.Unlock()
				resp, ok := tt.stubs[isbn]
				if !ok {
					t.Errorf("unexpected stub ISBN: %s", isbn)
					http.NotFound(w, r)
					return
				}
				w.Header().Set("Content-Type", "application/json")
				w.WriteHeader(resp.status)
				_, _ = w.Write([]byte(resp.body))
			}))
			defer stub.Close()

			router := testRouterWithOpenBDBaseURL(t, stub.URL)
			w := httptest.NewRecorder()
			req := httptest.NewRequest(http.MethodGet, tt.requestPath, nil)
			router.ServeHTTP(w, req)

			if w.Code != tt.wantStatus {
				t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
			}

			mu.Lock()
			gotISBNs := append([]string(nil), gotStubISBNs...)
			mu.Unlock()
			if tt.wantStubCalled && !equalStrings(gotISBNs, tt.wantStubISBNs) {
				t.Fatalf("stub ISBNs = %v, want %v", gotISBNs, tt.wantStubISBNs)
			}
			if !tt.wantStubCalled && len(gotISBNs) != 0 {
				t.Fatalf("stub should not be called, got ISBNs %v", gotISBNs)
			}

			if tt.wantStatus == http.StatusOK {
				var body []map[string]any
				if err := json.Unmarshal(w.Body.Bytes(), &body); err != nil {
					t.Fatal(err)
				}
				if len(body) != tt.wantLen {
					t.Fatalf("len(body) = %d, want %d: %+v", len(body), tt.wantLen, body)
				}
				if tt.assertBody != nil {
					tt.assertBody(t, body)
				}
				return
			}

			var problem struct {
				Title  string              `json:"title"`
				Detail string              `json:"detail"`
				Errors []appTestFieldError `json:"errors"`
			}
			if err := json.Unmarshal(w.Body.Bytes(), &problem); err != nil {
				t.Fatal(err)
			}
			if tt.wantTitle != "" && problem.Title != tt.wantTitle {
				t.Fatalf("title = %q, want %q; body = %s", problem.Title, tt.wantTitle, w.Body.String())
			}
			if tt.wantDetail != "" && problem.Detail != tt.wantDetail {
				t.Fatalf("detail = %q, want %q; body = %s", problem.Detail, tt.wantDetail, w.Body.String())
			}
			if tt.wantStatus == http.StatusBadRequest && !appContainsField(problem.Errors, "isbn") {
				t.Fatalf("errors should include isbn: %+v", problem.Errors)
			}
		})
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

func testRouterWithOpenBDBaseURL(t *testing.T, baseURL string) http.Handler {
	t.Helper()
	t.Setenv("OPENBD_BASE_URL", baseURL)
	sqlDB, err := db.OpenMemory()
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { sqlDB.Close() })
	cfg := config.Load()
	cfg.APIBodyLogEnabled = false
	cfg.SQLLogEnabled = false
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

func equalStrings(a, b []string) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}

func appContainsField(items []appTestFieldError, field string) bool {
	for _, item := range items {
		if item.Field == field {
			return true
		}
	}
	return false
}
