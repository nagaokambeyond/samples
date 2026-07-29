package handler

import (
	_ "embed"
	"net/http"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

//go:embed openapi.yaml
var openAPIYAML []byte

func (h *Handler) registerDocsRoutes(r *gin.Engine) {
	r.GET("/openapi.yaml", h.openapiYAML)
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler, ginSwagger.URL("/openapi.yaml")))
	r.GET("/scalar", h.scalar)
	r.GET("/scalar/", h.scalar)
}

func (h *Handler) openapiYAML(c *gin.Context) {
	c.Data(http.StatusOK, "application/yaml; charset=utf-8", openAPIYAML)
}

func (h *Handler) scalar(c *gin.Context) {
	c.Data(http.StatusOK, "text/html; charset=utf-8", []byte(`<!doctype html>
<html>
  <head>
    <title>書籍管理システム API Reference</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
  </head>
  <body>
    <div id="app"></div>
    <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
    <script>
      Scalar.createApiReference('#app', {
        url: '/openapi.yaml'
      })
    </script>
  </body>
</html>`))
}
