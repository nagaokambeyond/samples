package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) getOpenBD(c *gin.Context) {
	resp, err := h.openbd.Get(c.Request.Context(), c.Query("isbn"))
	if err != nil {
		writeProblem(c, err)
		return
	}
	c.JSON(http.StatusOK, resp)
}
