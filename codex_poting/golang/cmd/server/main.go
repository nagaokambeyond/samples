package main

import (
	"log"

	"codex-poting/golang/internal/app"
	"codex-poting/golang/internal/applog"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/db"
)

func main() {
	cfg := config.Load()
	applog.Configure(cfg.LogLevel, cfg.LogFormat)
	sqlDB, err := db.Open(cfg.DatabasePath)
	if err != nil {
		log.Fatal(err)
	}
	defer sqlDB.Close()
	if err := app.NewRouter(cfg, sqlDB).Run(":" + cfg.Port); err != nil {
		log.Fatal(err)
	}
}
