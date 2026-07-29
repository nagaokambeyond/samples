package service

import (
	"database/sql"
	"regexp"
	"time"

	appdb "codex-poting/golang/internal/db"
	"codex-poting/golang/internal/dbsqlc"
)

var isbn13 = regexp.MustCompile(`^\d{13}$`)

type Service struct {
	db       *sql.DB
	q        *dbsqlc.Queries
	pageSize int64
	sqlLog   bool
}

func New(db *sql.DB, pageSize int64, sqlLog bool) *Service {
	return &Service{db: db, q: dbsqlc.New(appdb.NewSQLLogger(db, sqlLog)), pageSize: pageSize, sqlLog: sqlLog}
}

func (s *Service) txQueries(tx *sql.Tx) *dbsqlc.Queries {
	return dbsqlc.New(appdb.NewSQLLogger(tx, s.sqlLog))
}

func today() string {
	return time.Now().Format("2006-01-02")
}

func nowString() string {
	return time.Now().Format(time.RFC3339)
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
