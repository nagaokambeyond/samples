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
	http    Doer
}

type Doer interface {
	Do(*http.Request) (*http.Response, error)
}

func New(baseURL string) *Client {
	return NewWithHTTPClient(baseURL, &http.Client{Timeout: 10 * time.Second})
}

func NewWithHTTPClient(baseURL string, client Doer) *Client {
	return &Client{baseURL: baseURL, http: client}
}

type BookResponse struct {
	Onix    *OnixResponse    `json:"onix,omitempty"`
	Hanmoto *HanmotoResponse `json:"hanmoto,omitempty"`
	Summary *SummaryResponse `json:"summary,omitempty"`
}

type OnixResponse struct {
	RecordReference   string                     `json:"RecordReference,omitempty"`
	NotificationType  string                     `json:"NotificationType,omitempty"`
	ProductIdentifier *ProductIdentifierResponse `json:"ProductIdentifier,omitempty"`
	DescriptiveDetail map[string]interface{}     `json:"DescriptiveDetail,omitempty"`
	CollateralDetail  map[string]interface{}     `json:"CollateralDetail,omitempty"`
	PublishingDetail  map[string]interface{}     `json:"PublishingDetail,omitempty"`
	ProductSupply     map[string]interface{}     `json:"ProductSupply,omitempty"`
}

type ProductIdentifierResponse struct {
	ProductIDType string `json:"ProductIDType,omitempty"`
	IDValue       string `json:"IDValue,omitempty"`
}

type HanmotoResponse struct {
	Datekoukai   string                   `json:"datekoukai,omitempty"`
	Datemodified string                   `json:"datemodified,omitempty"`
	Datecreated  string                   `json:"datecreated,omitempty"`
	Dateshuppan  string                   `json:"dateshuppan,omitempty"`
	Reviews      []map[string]interface{} `json:"reviews,omitempty"`
	Hanmotoinfo  map[string]interface{}   `json:"hanmotoinfo,omitempty"`
}

type SummaryResponse struct {
	Isbn      string `json:"isbn,omitempty"`
	Title     string `json:"title,omitempty"`
	Volume    string `json:"volume,omitempty"`
	Series    string `json:"series,omitempty"`
	Publisher string `json:"publisher,omitempty"`
	Pubdate   string `json:"pubdate,omitempty"`
	Cover     string `json:"cover,omitempty"`
	Author    string `json:"author,omitempty"`
}

func (c *Client) Get(ctx context.Context, isbn string) ([]BookResponse, error) {
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
		return nil, externalAPIError()
	}
	defer resp.Body.Close()
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return nil, externalAPIError()
	}
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, externalAPIError()
	}
	if string(body) == "null" {
		return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
	}
	var raw []*BookResponse
	if err := json.Unmarshal(body, &raw); err != nil {
		return nil, externalAPIError()
	}
	if len(raw) == 0 {
		return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
	}
	result := make([]BookResponse, 0, len(raw))
	for _, item := range raw {
		if item == nil {
			return nil, problem.New(http.StatusNotFound, "OpenBD書誌なし", "")
		}
		result = append(result, *item)
	}
	return result, nil
}

func externalAPIError() error {
	return problem.New(http.StatusBadGateway, "外部API呼び出しエラー", "OpenBD APIの呼び出しに失敗しました")
}
