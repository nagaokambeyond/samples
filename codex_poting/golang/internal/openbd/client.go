package openbd

import (
	"context"
	"encoding/json"
	"io"
	"net/http"
	"net/url"
	"regexp"
	"time"

	"codex-poting/golang/internal/problem"
)

var isbnList = regexp.MustCompile(`^\d{13}(,\d{13})*$`)

type Client struct {
	baseURL string
	http    *http.Client
}

func New(baseURL string) *Client {
	return &Client{baseURL: baseURL, http: &http.Client{Timeout: 10 * time.Second}}
}

func (c *Client) Get(ctx context.Context, isbn string) ([]map[string]interface{}, error) {
	if isbn == "" || !isbnList.MatchString(isbn) {
		return nil, problem.Request([]problem.FieldError{{Field: "isbn", Message: "13桁ISBNまたはカンマ区切りの13桁ISBNを指定してください"}}, "getBooksByIsbn.isbn: 13桁ISBNまたはカンマ区切りの13桁ISBNを指定してください")
	}
	u, err := url.Parse(c.baseURL + "/v1/get")
	if err != nil {
		return nil, err
	}
	q := u.Query()
	q.Set("isbn", isbn)
	u.RawQuery = q.Encode()
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, u.String(), nil)
	if err != nil {
		return nil, err
	}
	resp, err := c.http.Do(req)
	if err != nil {
		return nil, problem.New(http.StatusBadGateway, "外部API呼び出しエラー", "OpenBD APIの呼び出しに失敗しました")
	}
	defer resp.Body.Close()
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return nil, problem.New(http.StatusBadGateway, "外部API呼び出しエラー", "OpenBD APIの呼び出しに失敗しました")
	}
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}
	if string(body) == "null" {
		return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
	}
	var raw []map[string]interface{}
	if err := json.Unmarshal(body, &raw); err != nil {
		return nil, err
	}
	if len(raw) == 0 {
		return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
	}
	for _, item := range raw {
		if item == nil {
			return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
		}
	}
	return raw, nil
}
