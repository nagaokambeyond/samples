package service

import (
	"fmt"

	"codex-poting/golang/internal/problem"
)

func checkTitle(v *string, fields *[]problem.FieldError) {
	if v == nil || *v == "" || len([]rune(*v)) > 100 {
		*fields = append(*fields, problem.FieldError{Field: "title", Message: "1 から 100 の間のサイズにしてください"})
	}
}

func checkDate(field string, v *string, fields *[]problem.FieldError) {
	if v == nil || *v == "" {
		*fields = append(*fields, problem.FieldError{Field: field, Message: "null は許可されていません"})
		return
	}
	if _, err := parseDate(*v); err != nil {
		*fields = append(*fields, problem.FieldError{Field: field, Message: "日付形式が不正です"})
	}
}

func checkISBN(field string, v *string, fields *[]problem.FieldError) {
	if v == nil || !isbn13.MatchString(*v) {
		*fields = append(*fields, problem.FieldError{Field: field, Message: "13桁の数字にしてください"})
	}
}

func checkRequiredInt(field string, v *int64, fields *[]problem.FieldError) {
	if v == nil {
		*fields = append(*fields, problem.FieldError{Field: field, Message: "null は許可されていません"})
	}
}

func checkRange(field string, v *int64, min, max int64, fields *[]problem.FieldError) {
	if v == nil {
		*fields = append(*fields, problem.FieldError{Field: field, Message: "null は許可されていません"})
		return
	}
	if *v < min || *v > max {
		*fields = append(*fields, problem.FieldError{Field: field, Message: fmt.Sprintf("%d から %d の値にしてください", min, max)})
	}
}
