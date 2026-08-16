package app

import (
	"bytes"
	"database/sql"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"sync"
	"testing"
	"time"

	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
	"codex-poting/golang/internal/openbd"
	"codex-poting/golang/internal/service"
)

type acceptanceClock struct {
	mu  sync.Mutex
	now time.Time
}

func (c *acceptanceClock) Now() time.Time {
	c.mu.Lock()
	defer c.mu.Unlock()
	return c.now
}

func (c *acceptanceClock) Set(value time.Time) {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.now = value
}

type acceptanceEnv struct {
	db     *sql.DB
	router http.Handler
	clock  *acceptanceClock
}

func newAcceptanceEnv(t *testing.T, configure func(*config.Config), client openbd.Doer) *acceptanceEnv {
	t.Helper()
	sqlDB, err := db.OpenMemory()
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = sqlDB.Close() })
	clock := &acceptanceClock{now: time.Date(2026, time.August, 16, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60))}
	cfg := config.Load()
	cfg.APIBodyLogEnabled = false
	cfg.SQLLogEnabled = false
	cfg.TimeZone = "Asia/Tokyo"
	cfg.OpenBDBaseURL = "http://openbd.test"
	if configure != nil {
		configure(&cfg)
	}
	return &acceptanceEnv{
		db:     sqlDB,
		router: NewRouterWithOptions(cfg, sqlDB, Options{Clock: clock.Now, OpenBDHTTPClient: client}),
		clock:  clock,
	}
}

func acceptanceRequest(t *testing.T, router http.Handler, method, path, token string, body interface{}) *httptest.ResponseRecorder {
	t.Helper()
	var payload []byte
	var err error
	switch value := body.(type) {
	case nil:
	case []byte:
		payload = value
	default:
		payload, err = json.Marshal(value)
		if err != nil {
			t.Fatal(err)
		}
	}
	req := httptest.NewRequest(method, path, bytes.NewReader(payload))
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	if token != "" {
		req.Header.Set("Authorization", "Bearer "+token)
	}
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	return w
}

func acceptanceDecode[T any](t *testing.T, w *httptest.ResponseRecorder) T {
	t.Helper()
	var result T
	if err := json.Unmarshal(w.Body.Bytes(), &result); err != nil {
		t.Fatalf("decode response: %v; body=%s", err, w.Body.String())
	}
	return result
}

func acceptanceToken(t *testing.T, router http.Handler) string {
	t.Helper()
	w := acceptanceRequest(t, router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{
		"username": "admin", "password": "password",
	})
	if w.Code != http.StatusOK {
		t.Fatalf("login status=%d body=%s", w.Code, w.Body.String())
	}
	response := acceptanceDecode[struct {
		AccessToken string `json:"accessToken"`
	}](t, w)
	return response.AccessToken
}

func acceptanceCreateBook(t *testing.T, env *acceptanceEnv, token, isbn string) service.BookResponse {
	t.Helper()
	w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", token, map[string]interface{}{
		"title": "テスト書籍", "author": "Tester", "releaseDate": "2026-01-01",
		"publisherId": 1, "genreId": 5, "isbn": isbn, "salesUnitPrice": 1200,
	})
	if w.Code != http.StatusOK {
		t.Fatalf("create book status=%d body=%s", w.Code, w.Body.String())
	}
	return acceptanceDecode[service.BookResponse](t, w)
}

type acceptanceProblem struct {
	Title    string              `json:"title"`
	Status   int                 `json:"status"`
	Detail   string              `json:"detail"`
	Instance string              `json:"instance"`
	Errors   []appTestFieldError `json:"errors"`
}

func acceptanceHasField(problem acceptanceProblem, field string) bool {
	return appContainsField(problem.Errors, field)
}

func acceptanceCount(t *testing.T, sqlDB *sql.DB, query string, args ...interface{}) int64 {
	t.Helper()
	var count int64
	if err := sqlDB.QueryRow(query, args...).Scan(&count); err != nil {
		t.Fatal(err)
	}
	return count
}
