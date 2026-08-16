package app

import (
	"context"
	"net/http"
	"net/http/httptest"
	"path/filepath"
	"strings"
	"testing"
	"time"

	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
)

func TestErrorResponseAcceptance(t *testing.T) {
	t.Run("ERROR-001 request body validation", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", acceptanceToken(t, env.router), map[string]interface{}{})
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || problem.Title != "リクエストバリデーションエラー" || len(problem.Errors) == 0 {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("ERROR-002 query validation", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?page=-1", "", nil)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || !acceptanceHasField(problem, "page") {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("ERROR-003 data not found", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/999", "", nil)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusNotFound || problem.Title != "該当データなし" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("ERROR-004 data validation", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", acceptanceToken(t, env.router), validBookBody("0000000000001"))
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || problem.Title != "データバリデーション" || problem.Detail == "" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("ERROR-005 optimistic update conflict", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", acceptanceToken(t, env.router), updateBookBody(1, 999, "0000000000001"))
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusConflict || problem.Title != "更新競合" || problem.Detail != "他ユーザーによって更新されています" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("ERROR-006 problem content type and common fields", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		responses := []*httptest.ResponseRecorder{
			acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", token, map[string]interface{}{}),
			acceptanceRequest(t, env.router, http.MethodGet, "/api/books/999", "", nil),
			acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", token, updateBookBody(1, 999, "0000000000001")),
		}
		for _, w := range responses {
			problem := acceptanceDecode[acceptanceProblem](t, w)
			if !strings.HasPrefix(w.Header().Get("Content-Type"), "application/problem+json") || problem.Status != w.Code || problem.Title == "" || problem.Instance == "" {
				t.Fatalf("headers=%v problem=%+v", w.Header(), problem)
			}
		}
	})

	t.Run("ERROR-007 malformed JSON stops authentication", func(t *testing.T) {
		env := newAcceptanceEnv(t, func(cfg *config.Config) { cfg.LoginRateLimitDailyLimit = 1 }, nil)
		bad := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", []byte(`{"username":`))
		if bad.Code != http.StatusBadRequest {
			t.Fatalf("malformed status=%d body=%s", bad.Code, bad.Body.String())
		}
		valid := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "password"})
		if valid.Code != http.StatusOK {
			t.Fatalf("valid login status=%d body=%s", valid.Code, valid.Body.String())
		}
	})

	t.Run("ERROR-008 database lock conflict leaves no partial update", func(t *testing.T) {
		path := filepath.Join(t.TempDir(), "lock.db")
		apiDB, err := db.Open(path)
		if err != nil {
			t.Fatal(err)
		}
		t.Cleanup(func() { _ = apiDB.Close() })
		apiDB.SetMaxOpenConns(1)
		if _, err := apiDB.Exec("PRAGMA busy_timeout = 1"); err != nil {
			t.Fatal(err)
		}
		lockerDB, err := db.Open(path)
		if err != nil {
			t.Fatal(err)
		}
		t.Cleanup(func() { _ = lockerDB.Close() })
		lockerDB.SetMaxOpenConns(1)

		clock := &acceptanceClock{now: time.Date(2026, 8, 16, 12, 0, 0, 0, time.FixedZone("JST", 9*60*60))}
		cfg := config.Load()
		cfg.APIBodyLogEnabled = false
		cfg.SQLLogEnabled = false
		router := NewRouterWithOptions(cfg, apiDB, Options{Clock: clock.Now})
		token := acceptanceToken(t, router)

		var beforeTitle, beforeUpdateAt string
		var beforeVersion int64
		if err := apiDB.QueryRow("SELECT title, update_at, version FROM book WHERE id = 1").Scan(&beforeTitle, &beforeUpdateAt, &beforeVersion); err != nil {
			t.Fatal(err)
		}
		conn, err := lockerDB.Conn(context.Background())
		if err != nil {
			t.Fatal(err)
		}
		defer conn.Close()
		if _, err := conn.ExecContext(context.Background(), "BEGIN IMMEDIATE"); err != nil {
			t.Fatal(err)
		}
		defer conn.ExecContext(context.Background(), "ROLLBACK")

		w := acceptanceRequest(t, router, http.MethodPost, "/api/books/update", token, updateBookBody(1, beforeVersion, "0000000000001"))
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusConflict || problem.Title != "更新競合" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
		var afterTitle, afterUpdateAt string
		var afterVersion int64
		if err := apiDB.QueryRow("SELECT title, update_at, version FROM book WHERE id = 1").Scan(&afterTitle, &afterUpdateAt, &afterVersion); err != nil {
			t.Fatal(err)
		}
		if afterTitle != beforeTitle || afterUpdateAt != beforeUpdateAt || afterVersion != beforeVersion {
			t.Fatalf("book changed: before=%s/%s/%d after=%s/%s/%d", beforeTitle, beforeUpdateAt, beforeVersion, afterTitle, afterUpdateAt, afterVersion)
		}
	})
}
