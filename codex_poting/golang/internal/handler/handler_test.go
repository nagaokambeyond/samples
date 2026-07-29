package handler

import (
	"net/http"
	"testing"

	"codex-poting/golang/internal/auth"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
	"codex-poting/golang/internal/openbd"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

type testFieldError struct {
	Field string `json:"field"`
}

func testHandlerRouter(t *testing.T) (http.Handler, func()) {
	t.Helper()
	gin.SetMode(gin.TestMode)
	sqlDB, err := db.OpenMemory()
	if err != nil {
		t.Fatal(err)
	}
	cfg := config.Load()
	cfg.OpenBDBaseURL = "http://127.0.0.1:1"
	cfg.APIBodyLogEnabled = false
	cfg.SQLLogEnabled = false
	tokenService := auth.NewTokenService(cfg.JWTSecret, cfg.JWTExpiresInSeconds)
	limiter := auth.NewRateLimiter(cfg.LoginRateLimitEnabled, cfg.LoginRateLimitDailyLimit, cfg.LoginRateLimitZone)
	svc := service.New(sqlDB, cfg.SearchPageSize, cfg.SQLLogEnabled)
	router := New(cfg, svc, tokenService, limiter, openbd.New(cfg.OpenBDBaseURL)).Router()
	return router, func() { sqlDB.Close() }
}

func containsField(items []testFieldError, field string) bool {
	for _, item := range items {
		if item.Field == field {
			return true
		}
	}
	return false
}
