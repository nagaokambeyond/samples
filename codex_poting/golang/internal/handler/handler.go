package handler

import (
	"errors"
	"net/http"
	"strconv"
	"strings"

	"codex-poting/golang/internal/auth"
	"codex-poting/golang/internal/config"
	"codex-poting/golang/internal/openbd"
	"codex-poting/golang/internal/problem"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	cfg     config.Config
	svc     *service.Service
	tokens  *auth.TokenService
	limiter *auth.RateLimiter
	openbd  *openbd.Client
}

func New(cfg config.Config, svc *service.Service, tokens *auth.TokenService, limiter *auth.RateLimiter, openbdClient *openbd.Client) *Handler {
	return &Handler{cfg: cfg, svc: svc, tokens: tokens, limiter: limiter, openbd: openbdClient}
}

func (h *Handler) Router() *gin.Engine {
	r := gin.New()
	r.Use(h.accessLogger(), gin.Recovery(), h.bodyLogger())
	h.registerDocsRoutes(r)
	r.POST("/api/auth/login", h.login)
	r.GET("/api/books/:id", h.getBook)
	r.GET("/api/books/search", h.searchBooks)
	r.GET("/api/books/openbd", h.getOpenBD)
	protected := r.Group("/")
	protected.Use(h.authRequired())
	protected.POST("/api/auth/login-rate-limit/reset", h.resetLoginRateLimit)
	protected.POST("/api/books/create", h.createBook)
	protected.POST("/api/books/update", h.updateBook)
	protected.POST("/api/books/:id/sales-unit-prices", h.createSalesUnitPrice)
	protected.DELETE("/api/books/:id", h.deleteBook)
	protected.POST("/api/purchases/create", h.createPurchase)
	return r
}

func (h *Handler) authRequired() gin.HandlerFunc {
	return func(c *gin.Context) {
		header := c.GetHeader("Authorization")
		if !strings.HasPrefix(header, "Bearer ") {
			writeProblem(c, problem.Unauthorized("Unauthorized"))
			c.Abort()
			return
		}
		if err := h.tokens.Validate(strings.TrimPrefix(header, "Bearer ")); err != nil {
			writeProblem(c, problem.Unauthorized("Invalid bearer token"))
			c.Abort()
			return
		}
		c.Next()
	}
}

func pathID(c *gin.Context, name string) (int64, bool) {
	id, err := strconv.ParseInt(c.Param(name), 10, 64)
	if err != nil {
		writeProblem(c, problem.New(http.StatusBadRequest, "Bad Request", "Failed to convert '"+name+"' with value: '"+c.Param(name)+"'"))
		return 0, false
	}
	return id, true
}

func writeProblem(c *gin.Context, err error) {
	if err == nil {
		return
	}
	var pe *problem.Error
	if errors.As(err, &pe) {
		d := pe.Detail
		d.Instance = c.Request.URL.Path
		c.Header("Content-Type", "application/problem+json")
		c.JSON(d.Status, d)
		return
	}
	d := problem.Detail{Status: http.StatusInternalServerError, Title: "Internal Server Error", Detail: err.Error(), Instance: c.Request.URL.Path}
	c.Header("Content-Type", "application/problem+json")
	c.JSON(http.StatusInternalServerError, d)
}
