package service

import (
	"context"
	"fmt"
	"time"

	"codex-poting/golang/internal/dbsqlc"
	"codex-poting/golang/internal/problem"
)

func validateBookSearchCorrelation(from, to string) error {
	if (from == "") != (to == "") {
		return problem.New(400, "相関バリデーション", "発売日付From、発売日付To両方設定してください。")
	}
	if from == "" {
		return nil
	}
	if _, err := parseDate(from); err != nil {
		return problem.Request([]problem.FieldError{{Field: "releaseDateFrom", Message: "日付形式が不正です"}}, "")
	}
	if _, err := parseDate(to); err != nil {
		return problem.Request([]problem.FieldError{{Field: "releaseDateTo", Message: "日付形式が不正です"}}, "")
	}
	if from > to {
		return problem.New(400, "相関バリデーション", "発売日付From＜＝発売日付Toにしてください。")
	}
	return nil
}

func validateBookCreate(req BookCreateRequest) []problem.FieldError {
	var fields []problem.FieldError
	checkTitle(req.Title, &fields)
	checkAuthor(req.Author, &fields)
	checkDate("releaseDate", req.ReleaseDate, &fields)
	checkRequiredInt("publisherId", req.PublisherID, &fields)
	checkRequiredInt("genreId", req.GenreID, &fields)
	checkISBN("isbn", req.Isbn, &fields)
	checkRange("salesUnitPrice", req.SalesUnitPrice, 1, 10000, &fields)
	return fields
}

func validateBookUpdate(req BookUpdateRequest) []problem.FieldError {
	var fields []problem.FieldError
	checkRequiredInt("id", req.ID, &fields)
	checkTitle(req.Title, &fields)
	checkAuthor(req.Author, &fields)
	checkDate("releaseDate", req.ReleaseDate, &fields)
	checkRequiredInt("publisherId", req.PublisherID, &fields)
	checkRequiredInt("genreId", req.GenreID, &fields)
	checkISBN("isbn", req.Isbn, &fields)
	checkRequiredInt("version", req.Version, &fields)
	return fields
}

func validateSalesPrice(req SalesUnitPriceCreateRequest, now time.Time) []problem.FieldError {
	var fields []problem.FieldError
	checkRange("salesUnitPrice", req.SalesUnitPrice, 1, 10000, &fields)
	if req.EffectiveFrom == nil || *req.EffectiveFrom == "" {
		fields = append(fields, problem.FieldError{Field: "effectiveFrom", Message: "null は許可されていません"})
	} else if _, err := parseDate(*req.EffectiveFrom); err != nil || *req.EffectiveFrom <= now.Format("2006-01-02") {
		fields = append(fields, problem.FieldError{Field: "effectiveFrom", Message: "未来の日付にしてください"})
	}
	return fields
}

func (s *Service) validateBookRefs(ctx context.Context, q *dbsqlc.Queries, publisherID, genreID int64) error {
	if ok, err := q.ExistsPublisher(ctx, publisherID); err != nil {
		return err
	} else if !ok {
		return problem.DataValidation(fmt.Sprintf("参照先データが存在しません: publisher(id=%d)", publisherID))
	}
	if ok, err := q.ExistsGenre(ctx, genreID); err != nil {
		return err
	} else if !ok {
		return problem.DataValidation(fmt.Sprintf("参照先データが存在しません: book_genre(id=%d)", genreID))
	}
	return nil
}
