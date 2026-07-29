package config

import (
	"os"
	"strconv"
)

type Config struct {
	Port                     string
	DatabasePath             string
	AuthUsername             string
	AuthPassword             string
	JWTSecret                string
	JWTExpiresInSeconds      int64
	SearchPageSize           int64
	OpenBDBaseURL            string
	LoginRateLimitEnabled    bool
	LoginRateLimitDailyLimit int
	LoginRateLimitZone       string
	APIBodyLogEnabled        bool
	APIBodyLogMaxBytes       int64
	SQLLogEnabled            bool
	LogLevel                 string
	LogFormat                string
}

func Load() Config {
	return Config{
		Port:                     env("PORT", "8080"),
		DatabasePath:             env("DATABASE_PATH", "./data/app.db"),
		AuthUsername:             env("AUTH_USERNAME", "admin"),
		AuthPassword:             env("AUTH_PASSWORD", "password"),
		JWTSecret:                env("JWT_SECRET", "local-development-secret-change-me"),
		JWTExpiresInSeconds:      envInt64("JWT_EXPIRES_IN_SECONDS", 3600),
		SearchPageSize:           envInt64("SEARCH_PAGE_SIZE", 10),
		OpenBDBaseURL:            env("OPENBD_BASE_URL", "https://api.openbd.jp"),
		LoginRateLimitEnabled:    envBool("LOGIN_RATE_LIMIT_ENABLED", true),
		LoginRateLimitDailyLimit: envInt("LOGIN_RATE_LIMIT_DAILY_LIMIT", 10),
		LoginRateLimitZone:       env("LOGIN_RATE_LIMIT_ZONE", "Asia/Tokyo"),
		APIBodyLogEnabled:        envBool("API_BODY_LOG_ENABLED", true),
		APIBodyLogMaxBytes:       envInt64("API_BODY_LOG_MAX_BYTES", 8192),
		SQLLogEnabled:            envBool("SQL_LOG_ENABLED", true),
		LogLevel:                 env("LOG_LEVEL", "debug"),
		LogFormat:                env("LOG_FORMAT", "json"),
	}
}

func env(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func envInt64(key string, fallback int64) int64 {
	v, err := strconv.ParseInt(os.Getenv(key), 10, 64)
	if err != nil {
		return fallback
	}
	return v
}

func envInt(key string, fallback int) int {
	v, err := strconv.Atoi(os.Getenv(key))
	if err != nil {
		return fallback
	}
	return v
}

func envBool(key string, fallback bool) bool {
	v := os.Getenv(key)
	if v == "" {
		return fallback
	}
	parsed, err := strconv.ParseBool(v)
	if err != nil {
		return fallback
	}
	return parsed
}
