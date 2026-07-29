package problem

import (
	"errors"
	"net/http"
)

type FieldError struct {
	Field   string `json:"field"`
	Message string `json:"message"`
}

type Detail struct {
	Title    string       `json:"title"`
	Status   int          `json:"status"`
	Detail   string       `json:"detail,omitempty"`
	Instance string       `json:"instance,omitempty"`
	Errors   []FieldError `json:"errors,omitempty"`
}

type Error struct {
	Detail
}

func (e *Error) Error() string {
	return e.Title
}

func New(status int, title string, detail string) *Error {
	return &Error{Detail: Detail{Status: status, Title: title, Detail: detail}}
}

func Validation(fields []FieldError) *Error {
	return &Error{Detail: Detail{Status: http.StatusBadRequest, Title: "リクエストバリデーションエラー", Errors: fields}}
}

func Request(fields []FieldError, detail string) *Error {
	return &Error{Detail: Detail{Status: http.StatusBadRequest, Title: "リクエストエラー", Detail: detail, Errors: fields}}
}

func DataValidation(detail string) *Error {
	return New(http.StatusBadRequest, "データバリデーション", detail)
}

func NotFound() *Error {
	return New(http.StatusNotFound, "該当データなし", "")
}

func Conflict() *Error {
	return New(http.StatusConflict, "更新競合", "他ユーザーによって更新されています")
}

func Unauthorized(detail string) *Error {
	if detail == "" {
		detail = "Unauthorized"
	}
	return New(http.StatusUnauthorized, "Unauthorized", detail)
}

func As(err error) (*Error, bool) {
	var pe *Error
	if errors.As(err, &pe) {
		return pe, true
	}
	return nil, false
}
