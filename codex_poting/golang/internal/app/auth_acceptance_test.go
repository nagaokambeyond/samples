package app

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"codex-poting/golang/internal/config"
)

func TestAuthenticationAcceptance(t *testing.T) {
	t.Run("AUTH-001 login succeeds", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "password"})
		if w.Code != http.StatusOK {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
		body := acceptanceDecode[struct {
			Username, TokenType, AccessToken string
			ExpiresIn                        int64
		}](t, w)
		if body.Username != "admin" || body.TokenType != "Bearer" || body.AccessToken == "" || body.ExpiresIn != 3600 {
			t.Fatalf("unexpected response: %+v", body)
		}
	})

	t.Run("AUTH-002 login fails", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "wrong-password"})
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusUnauthorized || problem.Title != "認証エラー" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("AUTH-003 invalid login request", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": " ", "password": ""})
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || problem.Title != "リクエストバリデーションエラー" || !acceptanceHasField(problem, "username") || !acceptanceHasField(problem, "password") {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("AUTH-004 public API needs no token", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?page=0", "", nil)
		if w.Code != http.StatusOK {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("AUTH-005 protected API requires token", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", "", map[string]interface{}{})
		if w.Code != http.StatusUnauthorized {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("AUTH-006 daily login limit", func(t *testing.T) {
		env := newAcceptanceEnv(t, func(cfg *config.Config) { cfg.LoginRateLimitDailyLimit = 10 }, nil)
		for i := 0; i < 10; i++ {
			w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "wrong"})
			if w.Code != http.StatusUnauthorized {
				t.Fatalf("attempt %d status=%d", i+1, w.Code)
			}
		}
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "wrong"})
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusTooManyRequests || problem.Title != "リクエスト回数制限" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("AUTH-007 login limit reset", func(t *testing.T) {
		env := newAcceptanceEnv(t, func(cfg *config.Config) { cfg.LoginRateLimitDailyLimit = 10 }, nil)
		token := acceptanceToken(t, env.router)
		for i := 0; i < 9; i++ {
			_ = acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "wrong"})
		}
		limited := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "wrong"})
		if limited.Code != http.StatusTooManyRequests {
			t.Fatalf("expected limit, status=%d", limited.Code)
		}
		reset := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login-rate-limit/reset", token, nil)
		if reset.Code != http.StatusNoContent || reset.Body.Len() != 0 {
			t.Fatalf("reset status=%d body=%s", reset.Code, reset.Body.String())
		}
		if got := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login", "", map[string]interface{}{"username": "admin", "password": "password"}); got.Code != http.StatusOK {
			t.Fatalf("login after reset status=%d", got.Code)
		}
	})

	t.Run("AUTH-008 valid token authorizes", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login-rate-limit/reset", acceptanceToken(t, env.router), nil)
		if w.Code != http.StatusNoContent || w.Body.Len() != 0 {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("AUTH-009 malformed bearer token", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		req := httptest.NewRequest(http.MethodPost, "/api/auth/login-rate-limit/reset", nil)
		req.Header.Set("Authorization", "Basic invalid")
		w := httptest.NewRecorder()
		env.router.ServeHTTP(w, req)
		if w.Code != http.StatusUnauthorized {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("AUTH-010 tampered signature", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		last := "A"
		if strings.HasSuffix(token, last) {
			last = "B"
		}
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login-rate-limit/reset", token[:len(token)-1]+last, nil)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusUnauthorized || problem.Detail != "Invalid bearer token" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("AUTH-011 expired token", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		env.clock.Set(env.clock.Now().Add(3601 * time.Second))
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/auth/login-rate-limit/reset", token, nil)
		if w.Code != http.StatusUnauthorized {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})
}
