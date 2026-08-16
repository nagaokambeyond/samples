package service

import (
	"context"
	"database/sql"
	"errors"
	"regexp"
	"strings"
	"time"

	appdb "codex-poting/golang/internal/db"
	"codex-poting/golang/internal/dbsqlc"
	"codex-poting/golang/internal/problem"

	"modernc.org/sqlite"
	sqlite3 "modernc.org/sqlite/lib"
)

var isbn13 = regexp.MustCompile(`^\d{13}$`)

type Service struct {
	db       *sql.DB
	q        *dbsqlc.Queries
	pageSize int64
	sqlLog   bool
	now      func() time.Time
}

func New(db *sql.DB, pageSize int64, sqlLog bool) *Service {
	return NewWithClock(db, pageSize, sqlLog, time.Now)

}

func NewWithClock(db *sql.DB, pageSize int64, sqlLog bool, clock func() time.Time) *Service {
	if clock == nil {
		clock = time.Now
	}
	return &Service{db: db, q: dbsqlc.New(appdb.NewSQLLogger(db, sqlLog)), pageSize: pageSize, sqlLog: sqlLog, now: clock}
}

func (s *Service) txQueries(tx *sql.Tx) *dbsqlc.Queries {
	return dbsqlc.New(appdb.NewSQLLogger(tx, s.sqlLog))
}

func (s *Service) today() string {
	return s.now().Format("2006-01-02")
}

func (s *Service) nowString() string {
	return s.now().Format(time.RFC3339)
}

func nullableString(v *string) sql.NullString {
	if v == nil {
		return sql.NullString{}
	}
	return sql.NullString{String: *v, Valid: true}
}

func parseDate(v string) (time.Time, error) {
	return time.Parse("2006-01-02", v)
}

const writeRetryCount = 3

func withWriteRetry[T any](ctx context.Context, operation func() (T, error)) (T, error) {
	var zero T
	for attempt := 0; attempt < writeRetryCount; attempt++ {
		result, err := operation()
		if err == nil {
			return result, nil
		}
		if !isSQLiteLockError(err) {
			return zero, err
		}
		if attempt == writeRetryCount-1 {
			return zero, problem.Conflict()
		}
		select {
		case <-ctx.Done():
			return zero, ctx.Err()
		case <-time.After(time.Duration(attempt+1) * 25 * time.Millisecond):
		}
	}
	return zero, problem.Conflict()
}

func isSQLiteLockError(err error) bool {
	var sqliteErr *sqlite.Error
	if !errors.As(err, &sqliteErr) {
		return false
	}
	code := sqliteErr.Code() & 0xff
	return code == sqlite3.SQLITE_BUSY || code == sqlite3.SQLITE_LOCKED
}

func isSQLiteUniqueConstraintError(err error) bool {
	return err != nil && strings.Contains(err.Error(), "UNIQUE constraint failed")
}
