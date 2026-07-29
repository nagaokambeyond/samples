package handler

import (
	"bytes"
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

type bodyLogWriter struct {
	gin.ResponseWriter
	body *bytes.Buffer
	max  int64
}

func (w bodyLogWriter) Write(data []byte) (int, error) {
	if int64(w.body.Len()) < w.max {
		remaining := int(w.max) - w.body.Len()
		if remaining > len(data) {
			remaining = len(data)
		}
		w.body.Write(data[:remaining])
	}
	return w.ResponseWriter.Write(data)
}

func (w bodyLogWriter) WriteString(data string) (int, error) {
	if int64(w.body.Len()) < w.max {
		remaining := int(w.max) - w.body.Len()
		if remaining > len(data) {
			remaining = len(data)
		}
		w.body.WriteString(data[:remaining])
	}
	return w.ResponseWriter.WriteString(data)
}

func (h *Handler) bodyLogger() gin.HandlerFunc {
	return func(c *gin.Context) {
		if !h.cfg.APIBodyLogEnabled {
			c.Next()
			return
		}

		start := time.Now()
		requestBody := readRequestBody(c)
		responseBody := &bytes.Buffer{}
		c.Writer = &bodyLogWriter{ResponseWriter: c.Writer, body: responseBody, max: h.cfg.APIBodyLogMaxBytes}

		c.Next()

		slog.Debug(
			"api_body",
			"method", c.Request.Method,
			"path", c.Request.URL.Path,
			"query", c.Request.URL.RawQuery,
			"status", c.Writer.Status(),
			"latency", time.Since(start).String(),
			"requestBody", formatLoggedBody(requestBody, h.cfg.APIBodyLogMaxBytes),
			"responseBody", formatLoggedBody(responseBody.Bytes(), h.cfg.APIBodyLogMaxBytes),
		)
	}
}

func readRequestBody(c *gin.Context) []byte {
	if c.Request.Body == nil || c.Request.Body == http.NoBody {
		return nil
	}
	body, err := io.ReadAll(c.Request.Body)
	if err != nil {
		return []byte("<read error: " + err.Error() + ">")
	}
	c.Request.Body = io.NopCloser(bytes.NewReader(body))
	return body
}

func formatLoggedBody(body []byte, maxBytes int64) string {
	if len(body) == 0 {
		return `""`
	}
	truncated := int64(len(body)) > maxBytes
	if truncated {
		body = body[:maxBytes]
	}
	redacted := redactJSON(body)
	if truncated {
		return redacted + "...(truncated)"
	}
	return redacted
}

func redactJSON(body []byte) string {
	var v interface{}
	if err := json.Unmarshal(body, &v); err != nil {
		return strconvQuote(strings.TrimSpace(string(body)))
	}
	redactValue(v)
	encoded, err := json.Marshal(v)
	if err != nil {
		return strconvQuote(strings.TrimSpace(string(body)))
	}
	return string(encoded)
}

func redactValue(v interface{}) {
	switch x := v.(type) {
	case map[string]interface{}:
		for key, value := range x {
			if isSensitiveKey(key) {
				x[key] = "***"
				continue
			}
			redactValue(value)
		}
	case []interface{}:
		for _, item := range x {
			redactValue(item)
		}
	}
}

func isSensitiveKey(key string) bool {
	switch strings.ToLower(key) {
	case "password", "accesstoken", "access_token", "token", "authorization":
		return true
	default:
		return false
	}
}

func strconvQuote(v string) string {
	encoded, err := json.Marshal(v)
	if err != nil {
		return `""`
	}
	return string(encoded)
}
