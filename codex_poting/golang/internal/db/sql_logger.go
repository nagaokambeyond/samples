package db

import (
	"context"
	"database/sql"
	"encoding/json"
	"log/slog"
	"strings"
	"time"
)

type DBTX interface {
	ExecContext(context.Context, string, ...interface{}) (sql.Result, error)
	PrepareContext(context.Context, string) (*sql.Stmt, error)
	QueryContext(context.Context, string, ...interface{}) (*sql.Rows, error)
	QueryRowContext(context.Context, string, ...interface{}) *sql.Row
}

type SQLLogger struct {
	db      DBTX
	enabled bool
}

func NewSQLLogger(db DBTX, enabled bool) SQLLogger {
	return SQLLogger{db: db, enabled: enabled}
}

func (l SQLLogger) ExecContext(ctx context.Context, query string, args ...interface{}) (sql.Result, error) {
	start := time.Now()
	result, err := l.db.ExecContext(ctx, query, args...)
	l.log("exec", query, args, time.Since(start), err)
	return result, err
}

func (l SQLLogger) PrepareContext(ctx context.Context, query string) (*sql.Stmt, error) {
	start := time.Now()
	stmt, err := l.db.PrepareContext(ctx, query)
	l.log("prepare", query, nil, time.Since(start), err)
	return stmt, err
}

func (l SQLLogger) QueryContext(ctx context.Context, query string, args ...interface{}) (*sql.Rows, error) {
	start := time.Now()
	rows, err := l.db.QueryContext(ctx, query, args...)
	l.log("query", query, args, time.Since(start), err)
	return rows, err
}

func (l SQLLogger) QueryRowContext(ctx context.Context, query string, args ...interface{}) *sql.Row {
	start := time.Now()
	row := l.db.QueryRowContext(ctx, query, args...)
	l.log("query_row", query, args, time.Since(start), nil)
	return row
}

func (l SQLLogger) log(operation, query string, args []interface{}, latency time.Duration, err error) {
	if !l.enabled {
		return
	}
	if err != nil {
		slog.Warn("sql_exec", "operation", operation, "latency", latency.String(), "query", compactSQL(query), "args", formatSQLArgs(args), "err", err.Error())
		return
	}
	slog.Debug("sql_exec", "operation", operation, "latency", latency.String(), "query", compactSQL(query), "args", formatSQLArgs(args))
}

func compactSQL(query string) string {
	return strings.Join(strings.Fields(query), " ")
}

func formatSQLArgs(args []interface{}) string {
	if len(args) == 0 {
		return "[]"
	}
	encoded, err := json.Marshal(args)
	if err != nil {
		return `"<args marshal error>"`
	}
	return string(encoded)
}
