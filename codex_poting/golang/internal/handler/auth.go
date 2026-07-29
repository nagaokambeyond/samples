package handler

import (
	"net/http"
	"strings"

	"codex-poting/golang/internal/problem"

	"github.com/gin-gonic/gin"
)

type loginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func (h *Handler) login(c *gin.Context) {
	var req loginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		writeProblem(c, problem.Validation([]problem.FieldError{{Field: "username", Message: "空白は許可されていません"}, {Field: "password", Message: "空白は許可されていません"}}))
		return
	}
	var fields []problem.FieldError
	if strings.TrimSpace(req.Username) == "" {
		fields = append(fields, problem.FieldError{Field: "username", Message: "空白は許可されていません"})
	}
	if strings.TrimSpace(req.Password) == "" {
		fields = append(fields, problem.FieldError{Field: "password", Message: "空白は許可されていません"})
	}
	if len(fields) > 0 {
		writeProblem(c, problem.Validation(fields))
		return
	}
	if !h.limiter.Consume(req.Username) {
		writeProblem(c, problem.New(http.StatusTooManyRequests, "リクエスト回数制限", "ログインリクエスト回数が日次上限を超えました"))
		return
	}
	if req.Username != h.cfg.AuthUsername || req.Password != h.cfg.AuthPassword {
		writeProblem(c, problem.New(http.StatusUnauthorized, "認証エラー", "ユーザー名またはパスワードが不正です"))
		return
	}
	token, err := h.tokens.Issue(req.Username)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, gin.H{"username": req.Username, "tokenType": "Bearer", "accessToken": token, "expiresIn": h.cfg.JWTExpiresInSeconds})
}

func (h *Handler) resetLoginRateLimit(c *gin.Context) {
	h.limiter.Reset()
	c.Status(http.StatusNoContent)
}
