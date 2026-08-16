package app

import (
	"net/http"
	"strconv"
	"strings"
	"testing"

	"codex-poting/golang/internal/service"
)

func TestBookReadAcceptance(t *testing.T) {
	t.Run("BOOK-001 get book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/1", "", nil)
		book := acceptanceDecode[service.BookResponse](t, w)
		if w.Code != http.StatusOK || book.ID != 1 || book.Title != "Spring入門" || book.Author == nil || *book.Author != "Taro" ||
			book.ReleaseDate != "2020-01-01" || book.PublisherID != 1 || book.PublisherName != "◯◯書房" ||
			book.GenreID != 5 || book.GenreName != "工学" || book.Isbn != "0000000000001" || book.SalesUnitPrice != 1200 ||
			book.UpdateAt == "" || len(book.BookStockList) != 3 {
			t.Fatalf("status=%d book=%+v", w.Code, book)
		}
		for i, want := range []struct {
			storeID, quantity int64
			name              string
		}{{1, 10, "あ駅前店"}, {2, 20, "い駅前店"}, {3, 30, "う駅前店"}} {
			stock := book.BookStockList[i]
			if stock.BookStockStoreID != want.storeID || stock.StoreName != want.name || stock.BookStockQuantity != want.quantity {
				t.Fatalf("stock[%d]=%+v", i, stock)
			}
		}
	})

	t.Run("BOOK-002 missing book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/999", "", nil)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusNotFound || problem.Title != "該当データなし" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	searchCases := []struct {
		name        string
		path        string
		status      int
		page        int64
		total       int64
		totalPages  int64
		contentSize int
		title       string
		field       string
	}{
		{"BOOK-003 search without token", "/api/books/search?page=0", 200, 0, 21, 3, 10, "", ""},
		{"BOOK-004 keyword search", "/api/books/search?keyword=spring&page=0", 200, 0, 1, 1, 1, "", ""},
		{"BOOK-005 author prefix search", "/api/books/search?keyword=hana&page=0", 200, 0, 20, 2, 10, "", ""},
		{"BOOK-006 release date search", "/api/books/search?releaseDateFrom=2020-02-01&releaseDateTo=2020-02-01&page=0", 200, 0, 20, 2, 10, "", ""},
		{"BOOK-007 zero based page", "/api/books/search?page=1", 200, 1, 21, 3, 10, "", ""},
		{"BOOK-008 page out of range", "/api/books/search?page=3", 200, 3, 21, 3, 0, "", ""},
		{"BOOK-009 negative page", "/api/books/search?keyword=spring&page=-1", 400, 0, 0, 0, 0, "", "page"},
		{"BOOK-010 only release date from", "/api/books/search?releaseDateFrom=2020-01-01&page=0", 400, 0, 0, 0, 0, "相関バリデーション", ""},
		{"BOOK-011 invalid release date range", "/api/books/search?releaseDateFrom=2020-01-02&releaseDateTo=2020-01-01&page=0", 400, 0, 0, 0, 0, "相関バリデーション", ""},
	}
	for _, tt := range searchCases {
		t.Run(tt.name, func(t *testing.T) {
			env := newAcceptanceEnv(t, nil, nil)
			w := acceptanceRequest(t, env.router, http.MethodGet, tt.path, "", nil)
			if w.Code != tt.status {
				t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
			}
			if tt.status != http.StatusOK {
				problem := acceptanceDecode[acceptanceProblem](t, w)
				if tt.title != "" && problem.Title != tt.title {
					t.Fatalf("problem=%+v", problem)
				}
				if tt.field != "" && !acceptanceHasField(problem, tt.field) {
					t.Fatalf("problem=%+v", problem)
				}
				return
			}
			page := acceptanceDecode[service.BookPageResponse](t, w)
			if page.Page != tt.page || page.Size != 10 || page.TotalElements != tt.total || page.TotalPages != tt.totalPages || len(page.Content) != tt.contentSize {
				t.Fatalf("page=%+v", page)
			}
			if strings.Contains(tt.name, "BOOK-004") && (page.Content[0].ID != 1 || page.Content[0].Title != "Spring入門") {
				t.Fatalf("content=%+v", page.Content)
			}
			if strings.Contains(tt.name, "BOOK-005") {
				for _, book := range page.Content {
					if book.Author == nil || !strings.HasPrefix(strings.ToLower(*book.Author), "hana") {
						t.Fatalf("author mismatch: %+v", book)
					}
				}
			}
			if strings.Contains(tt.name, "BOOK-006") {
				for _, book := range page.Content {
					if book.ReleaseDate != "2020-02-01" {
						t.Fatalf("release date mismatch: %+v", book)
					}
				}
			}
		})
	}

	t.Run("BOOK-012 book without stock", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		book := acceptanceCreateBook(t, env, acceptanceToken(t, env.router), "9784000000612")
		w := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/"+formatID(book.ID), "", nil)
		got := acceptanceDecode[service.BookResponse](t, w)
		if w.Code != http.StatusOK || got.ID != book.ID || got.BookStockList == nil || len(got.BookStockList) != 0 {
			t.Fatalf("status=%d book=%+v", w.Code, got)
		}
	})

	t.Run("BOOK-013 aggregate multiple stocks", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		book := acceptanceDecode[service.BookResponse](t, acceptanceRequest(t, env.router, http.MethodGet, "/api/books/1", "", nil))
		if len(book.BookStockList) != 3 || book.BookStockList[0].ID >= book.BookStockList[1].ID || book.BookStockList[1].ID >= book.BookStockList[2].ID {
			t.Fatalf("stocks=%+v", book.BookStockList)
		}
	})

	t.Run("BOOK-014 stock join does not duplicate books", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		page := acceptanceDecode[service.BookPageResponse](t, acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?page=0", "", nil))
		seen := map[int64]bool{}
		for _, book := range page.Content {
			if seen[book.ID] {
				t.Fatalf("duplicate book id=%d", book.ID)
			}
			seen[book.ID] = true
		}
		if page.TotalElements != 21 || len(page.Content[0].BookStockList) != 3 {
			t.Fatalf("page=%+v", page)
		}
	})

	t.Run("BOOK-015 empty keyword and last page", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		plain := acceptanceDecode[service.BookPageResponse](t, acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?page=0", "", nil))
		empty := acceptanceDecode[service.BookPageResponse](t, acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?keyword=%20%20&page=0", "", nil))
		last := acceptanceDecode[service.BookPageResponse](t, acceptanceRequest(t, env.router, http.MethodGet, "/api/books/search?page=2", "", nil))
		if plain.TotalElements != empty.TotalElements || plain.TotalPages != empty.TotalPages || len(plain.Content) != len(empty.Content) || last.Page != 2 || len(last.Content) != 1 {
			t.Fatalf("plain=%+v empty=%+v last=%+v", plain, empty, last)
		}
	})
}

func TestBookWriteAcceptance(t *testing.T) {
	t.Run("BOOK-W-001 create book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		book := acceptanceCreateBook(t, env, acceptanceToken(t, env.router), "9784000000501")
		if book.ID <= 0 || book.Title != "テスト書籍" || book.Author == nil || *book.Author != "Tester" || book.Version != 1 || book.SalesUnitPrice != 1200 || len(book.BookStockList) != 0 {
			t.Fatalf("book=%+v", book)
		}
		if acceptanceCount(t, env.db, "SELECT count(*) FROM book_sales_unit_price_history WHERE book_id = ? AND sales_unit_price = 1200 AND effective_from = '2026-01-01' AND effective_to IS NULL", book.ID) != 1 {
			t.Fatal("initial price history was not created")
		}
	})

	invalidCases := []struct {
		name   string
		body   map[string]interface{}
		title  string
		field  string
		detail string
	}{
		{"BOOK-W-002 invalid body", map[string]interface{}{"title": "", "releaseDate": nil, "publisherId": nil, "genreId": nil, "isbn": nil, "salesUnitPrice": nil}, "リクエストバリデーションエラー", "title", ""},
		{"BOOK-W-003 invalid ISBN", validBookBody("invalid"), "リクエストバリデーションエラー", "isbn", ""},
		{"BOOK-W-004 duplicate ISBN", validBookBody("0000000000001"), "データバリデーション", "", "book(isbn=0000000000001)"},
		{"BOOK-W-005 missing publisher", func() map[string]interface{} { b := validBookBody("9784000000502"); b["publisherId"] = 999; return b }(), "データバリデーション", "", "publisher(id=999)"},
	}
	for _, tt := range invalidCases {
		t.Run(tt.name, func(t *testing.T) {
			env := newAcceptanceEnv(t, nil, nil)
			w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", acceptanceToken(t, env.router), tt.body)
			problem := acceptanceDecode[acceptanceProblem](t, w)
			if w.Code != http.StatusBadRequest || problem.Title != tt.title || (tt.field != "" && !acceptanceHasField(problem, tt.field)) || (tt.detail != "" && !strings.Contains(problem.Detail, tt.detail)) {
				t.Fatalf("status=%d problem=%+v", w.Code, problem)
			}
		})
	}

	t.Run("BOOK-W-006 update book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		created := acceptanceCreateBook(t, env, token, "9784000000506")
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", token, updateBookBody(created.ID, created.Version, created.Isbn))
		book := acceptanceDecode[service.BookResponse](t, w)
		if w.Code != http.StatusOK || book.Title != "本更新HTTP更新後" || book.Author == nil || *book.Author != "Saburo" || book.PublisherID != 2 || book.Version != created.Version+1 || book.SalesUnitPrice != 1200 {
			t.Fatalf("status=%d book=%+v", w.Code, book)
		}
	})

	t.Run("BOOK-W-007 update missing book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", acceptanceToken(t, env.router), updateBookBody(999, 1, "9784000000599"))
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusNotFound || problem.Title != "該当データなし" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("BOOK-W-008 version conflict", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", acceptanceToken(t, env.router), updateBookBody(1, 999999, "0000000000001"))
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusConflict || problem.Title != "更新競合" {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("BOOK-W-009 delete book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000509")
		deleted := acceptanceRequest(t, env.router, http.MethodDelete, "/api/books/"+formatID(book.ID), token, nil)
		got := acceptanceRequest(t, env.router, http.MethodGet, "/api/books/"+formatID(book.ID), "", nil)
		if deleted.Code != http.StatusOK || deleted.Body.Len() != 0 || got.Code != http.StatusNotFound {
			t.Fatalf("delete=%d get=%d", deleted.Code, got.Code)
		}
	})

	t.Run("BOOK-W-010 delete missing book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := acceptanceRequest(t, env.router, http.MethodDelete, "/api/books/999", acceptanceToken(t, env.router), nil)
		if w.Code != http.StatusNotFound {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("BOOK-W-011 create boundaries", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		minimum := validBookBody("9784000000511")
		minimum["title"], minimum["author"], minimum["salesUnitPrice"] = "A", "", 1
		maximum := validBookBody("9784000000512")
		maximum["title"], maximum["author"], maximum["salesUnitPrice"] = strings.Repeat("本", 100), strings.Repeat("著", 200), 10000
		for _, body := range []map[string]interface{}{minimum, maximum} {
			if w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", token, body); w.Code != http.StatusOK {
				t.Fatalf("boundary status=%d body=%s", w.Code, w.Body.String())
			}
		}
		invalid := validBookBody("9784000000513")
		invalid["title"], invalid["author"], invalid["salesUnitPrice"] = strings.Repeat("本", 101), strings.Repeat("著", 201), 0
		problem := acceptanceDecode[acceptanceProblem](t, acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", token, invalid))
		if !acceptanceHasField(problem, "title") || !acceptanceHasField(problem, "author") || !acceptanceHasField(problem, "salesUnitPrice") {
			t.Fatalf("problem=%+v", problem)
		}
		over := validBookBody("9784000000514")
		over["salesUnitPrice"] = 10001
		if w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", token, over); w.Code != http.StatusBadRequest {
			t.Fatalf("over max status=%d", w.Code)
		}
	})

	t.Run("BOOK-W-012 update missing reference rolls back", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000520")
		body := updateBookBody(book.ID, book.Version, book.Isbn)
		body["genreId"] = 999
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", token, body)
		var title string
		var version int64
		_ = env.db.QueryRow("SELECT title, version FROM book WHERE id = ?", book.ID).Scan(&title, &version)
		if w.Code != http.StatusBadRequest || title != book.Title || version != book.Version {
			t.Fatalf("status=%d title=%s version=%d", w.Code, title, version)
		}
	})

	t.Run("BOOK-W-013 update duplicate ISBN rolls back", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000521")
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", token, updateBookBody(book.ID, book.Version, "0000000000001"))
		var isbn string
		var version int64
		_ = env.db.QueryRow("SELECT isbn, version FROM book WHERE id = ?", book.ID).Scan(&isbn, &version)
		if w.Code != http.StatusBadRequest || isbn != book.Isbn || version != book.Version {
			t.Fatalf("status=%d isbn=%s version=%d", w.Code, isbn, version)
		}
	})

	t.Run("BOOK-W-014 ISBN and price stay unchanged", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		book := acceptanceCreateBook(t, env, token, "9784000000522")
		updated := acceptanceDecode[service.BookResponse](t, acceptanceRequest(t, env.router, http.MethodPost, "/api/books/update", token, updateBookBody(book.ID, book.Version, book.Isbn)))
		if updated.Isbn != book.Isbn || updated.SalesUnitPrice != 1200 || updated.Version != book.Version+1 || acceptanceCount(t, env.db, "SELECT count(*) FROM book_sales_unit_price_history WHERE book_id = ?", book.ID) != 1 {
			t.Fatalf("updated=%+v", updated)
		}
	})

	t.Run("BOOK-W-015 price insert failure rolls back book", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		_, err := env.db.Exec(`CREATE TRIGGER fail_initial_price BEFORE INSERT ON book_sales_unit_price_history WHEN NEW.book_id > 21 BEGIN SELECT RAISE(FAIL, 'injected price failure'); END`)
		if err != nil {
			t.Fatal(err)
		}
		beforeBooks := acceptanceCount(t, env.db, "SELECT count(*) FROM book")
		beforePrices := acceptanceCount(t, env.db, "SELECT count(*) FROM book_sales_unit_price_history")
		w := acceptanceRequest(t, env.router, http.MethodPost, "/api/books/create", acceptanceToken(t, env.router), validBookBody("9784000000598"))
		if w.Code < 500 || acceptanceCount(t, env.db, "SELECT count(*) FROM book") != beforeBooks || acceptanceCount(t, env.db, "SELECT count(*) FROM book_sales_unit_price_history") != beforePrices || acceptanceCount(t, env.db, "SELECT count(*) FROM book WHERE isbn = '9784000000598'") != 0 {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})
}

func validBookBody(isbn string) map[string]interface{} {
	return map[string]interface{}{
		"title": "テスト書籍", "author": "Tester", "releaseDate": "2026-01-01",
		"publisherId": 1, "genreId": 5, "isbn": isbn, "salesUnitPrice": 1200,
	}
}

func updateBookBody(id, version int64, isbn string) map[string]interface{} {
	return map[string]interface{}{
		"id": id, "title": "本更新HTTP更新後", "author": "Saburo", "releaseDate": "2026-02-01",
		"publisherId": 2, "genreId": 5, "isbn": isbn, "version": version,
	}
}

func formatID(id int64) string {
	return strconv.FormatInt(id, 10)
}
