package handler

import (
	"log/slog"
	"time"

	"github.com/gin-gonic/gin"
)

func (h *Handler) accessLogger() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		c.Next()
		slog.Info(
			"api_access",
			"method", c.Request.Method,
			"path", c.Request.URL.Path,
			"query", c.Request.URL.RawQuery,
			"status", c.Writer.Status(),
			"latency", time.Since(start).String(),
			"clientIP", c.ClientIP(),
		)
	}
}
