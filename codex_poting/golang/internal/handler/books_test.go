package handler

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestHandlerInvalidPathIDReturnsBadRequest(t *testing.T) {
	router, cleanup := testHandlerRouter(t)
	defer cleanup()

	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/api/books/abc", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	var resp struct {
		Title  string `json:"title"`
		Detail string `json:"detail"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &resp); err != nil {
		t.Fatal(err)
	}
	if resp.Title != "Bad Request" || resp.Detail == "" {
		t.Fatalf("unexpected response: %+v", resp)
	}
}

func TestHandlerSearchBooksNegativePageReturnsRequestError(t *testing.T) {
	router, cleanup := testHandlerRouter(t)
	defer cleanup()

	w := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/api/books/search?page=-1", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
	}
	var resp struct {
		Title  string           `json:"title"`
		Errors []testFieldError `json:"errors"`
	}
	if err := json.Unmarshal(w.Body.Bytes(), &resp); err != nil {
		t.Fatal(err)
	}
	if resp.Title != "リクエストエラー" || !containsField(resp.Errors, "page") {
		t.Fatalf("unexpected response: %+v", resp)
	}
}
