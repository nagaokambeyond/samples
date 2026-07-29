package handler

import (
	"net/http"
	"strconv"

	"codex-poting/golang/internal/problem"
	"codex-poting/golang/internal/service"

	"github.com/gin-gonic/gin"
)

func (h *Handler) getBook(c *gin.Context) {
	id, ok := pathID(c, "id")
	if !ok {
		return
	}
	book, err := h.svc.GetBook(c.Request.Context(), id)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, book)
}

func (h *Handler) searchBooks(c *gin.Context) {
	pageRaw := c.Query("page")
	page, err := strconv.ParseInt(pageRaw, 10, 64)
	if pageRaw == "" || err != nil {
		writeProblem(c, problem.Request([]problem.FieldError{{Field: "page", Message: "0 以上の値にしてください"}}, "page: 0 以上の値にしてください"))
		return
	}
	resp, err := h.svc.SearchBooks(c.Request.Context(), c.Query("keyword"), c.Query("releaseDateFrom"), c.Query("releaseDateTo"), page)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *Handler) createBook(c *gin.Context) {
	var req service.BookCreateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		writeProblem(c, problem.Validation([]problem.FieldError{{Field: "body", Message: "JSON形式が不正です"}}))
		return
	}
	resp, err := h.svc.CreateBook(c.Request.Context(), req)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *Handler) updateBook(c *gin.Context) {
	var req service.BookUpdateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		writeProblem(c, problem.Validation([]problem.FieldError{{Field: "body", Message: "JSON形式が不正です"}}))
		return
	}
	resp, err := h.svc.UpdateBook(c.Request.Context(), req)
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *Handler) deleteBook(c *gin.Context) {
	id, ok := pathID(c, "id")
	if !ok {
		return
	}
	if err := h.svc.DeleteBook(c.Request.Context(), id); err != nil {
		writeProblem(c, err)
		return
	}
	c.Status(http.StatusOK)
}

func (h *Handler) createSalesUnitPrice(c *gin.Context) {
	id, ok := pathID(c, "id")
	if !ok {
		return
	}
	var req service.SalesUnitPriceCreateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		writeProblem(c, problem.Validation([]problem.FieldError{{Field: "body", Message: "JSON形式が不正です"}}))
		return
	}
	if err := h.svc.CreateSalesUnitPrice(c.Request.Context(), id, req); err != nil {
		writeProblem(c, err)
		return
	}
	c.Status(http.StatusOK)
}
