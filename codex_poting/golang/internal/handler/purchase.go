package handler

import (
	"net/http"

	"codex-poting/golang/internal/problem"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

func (h *Handler) createPurchase(c *gin.Context) {
	var req service.PurchaseInvoiceCreateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		writeProblem(c, problem.Validation([]problem.FieldError{{Field: "body", Message: "JSON形式が不正です"}}))
		return
	}
	resp, err := h.svc.CreatePurchase(c.Request.Context(), req)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, resp)
}
