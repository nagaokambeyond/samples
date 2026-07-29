package app

import (
	"database/sql"

	"codex-poting/golang/internal/auth"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/handler"
	"codex-poting/golang/internal/openbd"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

func NewRouter(cfg config.Config, db *sql.DB) *gin.Engine {
	tokenService := auth.NewTokenService(cfg.JWTSecret, cfg.JWTExpiresInSeconds)
	limiter := auth.NewRateLimiter(cfg.LoginRateLimitEnabled, cfg.LoginRateLimitDailyLimit, cfg.LoginRateLimitZone)
	svc := service.New(db, cfg.SearchPageSize, cfg.SQLLogEnabled)
	return handler.New(cfg, svc, tokenService, limiter, openbd.New(cfg.OpenBDBaseURL)).Router()
}
