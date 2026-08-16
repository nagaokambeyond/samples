package app

import (
	"database/sql"
	"time"

	"codex-poting/golang/internal/auth"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/handler"
	"codex-poting/golang/internal/openbd"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

type Options struct {
	Clock            func() time.Time
	OpenBDHTTPClient openbd.Doer
}

func NewRouter(cfg config.Config, db *sql.DB) *gin.Engine {
	return NewRouterWithOptions(cfg, db, Options{})
}

func NewRouterWithOptions(cfg config.Config, db *sql.DB, options Options) *gin.Engine {
	clock := options.Clock
	if clock == nil {
		loc, err := time.LoadLocation(cfg.TimeZone)
		if err != nil {
			loc = time.Local
		}
		clock = func() time.Time { return time.Now().In(loc) }
	}
	tokenService := auth.NewTokenServiceWithClock(cfg.JWTSecret, cfg.JWTExpiresInSeconds, clock)
	limiter := auth.NewRateLimiterWithClock(cfg.LoginRateLimitEnabled, cfg.LoginRateLimitDailyLimit, cfg.LoginRateLimitZone, clock)
	svc := service.NewWithClock(db, cfg.SearchPageSize, cfg.SQLLogEnabled, clock)
	openBDClient := openbd.New(cfg.OpenBDBaseURL)
	if options.OpenBDHTTPClient != nil {
		openBDClient = openbd.NewWithHTTPClient(cfg.OpenBDBaseURL, options.OpenBDHTTPClient)
	}
	return handler.New(cfg, svc, tokenService, limiter, openBDClient).Router()
}
