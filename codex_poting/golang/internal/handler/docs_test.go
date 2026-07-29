package handler

import (
	"bytes"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestHandlerDocumentationEndpoints(t *testing.T) {
	router, cleanup := testHandlerRouter(t)
	defer cleanup()

	tests := []struct {
		name       string
		path       string
		wantStatus int
		wantBody   []byte
	}{
		{name: "openapi", path: "/openapi.yaml", wantStatus: http.StatusOK, wantBody: []byte("openapi: 3.0.3")},
		{name: "scalar", path: "/scalar", wantStatus: http.StatusOK, wantBody: []byte("Scalar.createApiReference")},
		{name: "swagger", path: "/swagger/index.html", wantStatus: http.StatusOK, wantBody: []byte("swagger-ui-bundle.js")},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			w := httptest.NewRecorder()
			req := httptest.NewRequest(http.MethodGet, tt.path, nil)
			router.ServeHTTP(w, req)
			if w.Code != tt.wantStatus {
				t.Fatalf("status = %d, body = %s", w.Code, w.Body.String())
			}
			if !bytes.Contains(w.Body.Bytes(), tt.wantBody) {
				t.Fatalf("body did not contain %q: %s", string(tt.wantBody), w.Body.String())
			}
		})
	}
}
