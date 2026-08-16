package app

import (
	"fmt"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"codex-poting/golang/internal/service"
)

func TestPurchaseAcceptance(t *testing.T) {
	t.Run("PURCHASE-001 create purchase", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		token := acceptanceToken(t, env.router)
		w := createPurchase(t, env, token, validPurchaseBody())
		response := acceptanceDecode[service.PurchaseInvoiceResponse](t, w)
		if w.Code != http.StatusOK || response.ID <= 0 || response.PurchaseInvoiceType != "PURCHASE" || response.ReturnPurchaseInvoiceID != nil ||
			response.PurchaseInvoiceDate != "2026-02-01" || response.SupplierID != 1 || response.ReceivingStoreID != 2 || response.PurchaseInvoiceAmount != 3500 || len(response.Detail) != 2 ||
			response.Detail[0].PurchaseInvoiceDetailBookID != 1 || response.Detail[0].PurchaseInvoiceDetailAmount != 2000 || response.Detail[1].PurchaseInvoiceDetailBookID != 2 || response.Detail[1].PurchaseInvoiceDetailAmount != 1500 {
			t.Fatalf("status=%d response=%+v", w.Code, response)
		}
		assertStockQuantity(t, env, 2, 1, 22)
		assertStockQuantity(t, env, 2, 2, 24)
		if acceptanceCount(t, env.db, "SELECT count(*) FROM book_stock_movement WHERE source_id = ? AND movement_type = 2 AND source_type = 1", response.ID) != 2 {
			t.Fatal("purchase movements were not created")
		}
	})

	t.Run("PURCHASE-002 create missing stock", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		body := validPurchaseBody()
		body["receivingStoreId"] = 4
		body["details"] = []map[string]interface{}{purchaseDetail("0000000000001", 1000, 2)}
		w := createPurchase(t, env, acceptanceToken(t, env.router), body)
		if w.Code != http.StatusOK {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
		assertStockQuantity(t, env, 4, 1, 2)
	})

	validationCases := []struct {
		name   string
		body   map[string]interface{}
		fields []string
	}{
		{"PURCHASE-003 invalid invoice", map[string]interface{}{"purchaseInvoiceDate": nil, "supplierId": nil, "receivingStoreId": nil, "details": []interface{}{}}, []string{"purchaseInvoiceDate", "supplierId", "receivingStoreId", "details"}},
		{"PURCHASE-004 invalid detail fields", map[string]interface{}{"purchaseInvoiceDate": "2026-02-01", "supplierId": 1, "receivingStoreId": 2, "details": []map[string]interface{}{{"purchaseInvoiceDetailIsbn": nil, "purchaseInvoiceDetailUnitPrice": 0, "purchaseInvoiceDetailQuantity": 1001}}}, []string{"details[0].purchaseInvoiceDetailIsbn", "details[0].purchaseInvoiceDetailUnitPrice", "details[0].purchaseInvoiceDetailQuantity"}},
		{"PURCHASE-005 invalid detail ISBN", map[string]interface{}{"purchaseInvoiceDate": "2026-02-01", "supplierId": 1, "receivingStoreId": 2, "details": []map[string]interface{}{purchaseDetail("000000000001", 1000, 2), purchaseDetail("00000000000A1", 1000, 2)}}, []string{"details[0].purchaseInvoiceDetailIsbn", "details[1].purchaseInvoiceDetailIsbn"}},
		{"PURCHASE-006 too many details", purchaseBodyWithDetails(11), []string{"details"}},
	}
	for _, tt := range validationCases {
		t.Run(tt.name, func(t *testing.T) {
			env := newAcceptanceEnv(t, nil, nil)
			w := createPurchase(t, env, acceptanceToken(t, env.router), tt.body)
			problem := acceptanceDecode[acceptanceProblem](t, w)
			if w.Code != http.StatusBadRequest || problem.Title != "リクエストバリデーションエラー" {
				t.Fatalf("status=%d problem=%+v", w.Code, problem)
			}
			for _, field := range tt.fields {
				if !acceptanceHasField(problem, field) {
					t.Fatalf("missing field %s: %+v", field, problem)
				}
			}
		})
	}

	referenceCases := []struct {
		name, detail string
		mutate       func(map[string]interface{})
	}{
		{"PURCHASE-007 missing supplier", "supplier(id=999)", func(body map[string]interface{}) { body["supplierId"] = 999 }},
		{"PURCHASE-008 missing store", "store(id=999)", func(body map[string]interface{}) { body["receivingStoreId"] = 999 }},
		{"PURCHASE-009 missing detail ISBN", "book(isbn=9999999999999)", func(body map[string]interface{}) {
			body["details"] = []map[string]interface{}{purchaseDetail("9999999999999", 1000, 2)}
		}},
	}
	for _, tt := range referenceCases {
		t.Run(tt.name, func(t *testing.T) {
			env := newAcceptanceEnv(t, nil, nil)
			body := validPurchaseBody()
			tt.mutate(body)
			w := createPurchase(t, env, acceptanceToken(t, env.router), body)
			problem := acceptanceDecode[acceptanceProblem](t, w)
			if w.Code != http.StatusBadRequest || problem.Title != "データバリデーション" || !strings.Contains(problem.Detail, tt.detail) {
				t.Fatalf("status=%d problem=%+v", w.Code, problem)
			}
		})
	}

	t.Run("PURCHASE-010 token required", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		if w := createPurchase(t, env, "", validPurchaseBody()); w.Code != http.StatusUnauthorized {
			t.Fatalf("status=%d body=%s", w.Code, w.Body.String())
		}
	})

	t.Run("PURCHASE-011 empty details", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		body := validPurchaseBody()
		body["details"] = []interface{}{}
		before := acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice")
		w := createPurchase(t, env, acceptanceToken(t, env.router), body)
		problem := acceptanceDecode[acceptanceProblem](t, w)
		if w.Code != http.StatusBadRequest || !acceptanceHasField(problem, "details") || acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice") != before {
			t.Fatalf("status=%d problem=%+v", w.Code, problem)
		}
	})

	t.Run("PURCHASE-012 exactly ten details", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		w := createPurchase(t, env, acceptanceToken(t, env.router), purchaseBodyWithDetails(10))
		response := acceptanceDecode[service.PurchaseInvoiceResponse](t, w)
		if w.Code != http.StatusOK || response.PurchaseInvoiceAmount != 1000 || len(response.Detail) != 10 || acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice_detail WHERE purchase_invoice_id = ?", response.ID) != 10 || acceptanceCount(t, env.db, "SELECT count(*) FROM book_stock_movement WHERE source_id = ?", response.ID) != 10 {
			t.Fatalf("status=%d response=%+v", w.Code, response)
		}
	})

	t.Run("PURCHASE-013 duplicate ISBN details accumulate", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		body := validPurchaseBody()
		body["details"] = []map[string]interface{}{purchaseDetail("0000000000001", 1000, 2), purchaseDetail("0000000000001", 800, 3)}
		var beforeQuantity, beforeVersion int64
		_ = env.db.QueryRow("SELECT book_stock_quantity, version FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 1").Scan(&beforeQuantity, &beforeVersion)
		w := createPurchase(t, env, acceptanceToken(t, env.router), body)
		response := acceptanceDecode[service.PurchaseInvoiceResponse](t, w)
		var afterQuantity, afterVersion int64
		_ = env.db.QueryRow("SELECT book_stock_quantity, version FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 1").Scan(&afterQuantity, &afterVersion)
		if w.Code != http.StatusOK || response.PurchaseInvoiceAmount != 4400 || response.Detail[0].PurchaseInvoiceDetailAmount != 2000 || response.Detail[1].PurchaseInvoiceDetailAmount != 2400 || afterQuantity != beforeQuantity+5 || afterVersion != beforeVersion+2 {
			t.Fatalf("status=%d response=%+v stock=%d/%d", w.Code, response, afterQuantity, afterVersion)
		}
	})

	t.Run("PURCHASE-014 relational integrity", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		body := validPurchaseBody()
		body["receivingStoreId"] = 4
		body["details"] = []map[string]interface{}{purchaseDetail("0000000000003", 1000, 2), purchaseDetail("0000000000001", 500, 3)}
		w := createPurchase(t, env, acceptanceToken(t, env.router), body)
		response := acceptanceDecode[service.PurchaseInvoiceResponse](t, w)
		if w.Code != http.StatusOK || response.UpdateAt == "" || response.PurchaseInvoiceAmount != 3500 || len(response.Detail) != 2 {
			t.Fatalf("status=%d response=%+v", w.Code, response)
		}
		for _, detail := range response.Detail {
			if detail.PurchaseInvoiceID != response.ID || detail.ID <= 0 || detail.UpdateAt == "" || acceptanceCount(t, env.db, "SELECT count(*) FROM book_stock_movement WHERE source_id = ? AND source_detail_id = ? AND store_id = 4 AND book_id = ?", response.ID, detail.ID, detail.PurchaseInvoiceDetailBookID) != 1 {
				t.Fatalf("detail=%+v", detail)
			}
		}
		assertStockQuantity(t, env, 4, 1, 3)
		assertStockQuantity(t, env, 4, 3, 14)
	})

	t.Run("PURCHASE-015 detail failure rolls back all changes", func(t *testing.T) {
		env := newAcceptanceEnv(t, nil, nil)
		_, err := env.db.Exec(`CREATE TRIGGER fail_second_stock BEFORE UPDATE ON book_stock WHEN NEW.book_stock_book_id = 2 BEGIN SELECT RAISE(FAIL, 'injected stock failure'); END`)
		if err != nil {
			t.Fatal(err)
		}
		beforeInvoices := acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice")
		beforeDetails := acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice_detail")
		beforeMovements := acceptanceCount(t, env.db, "SELECT count(*) FROM book_stock_movement")
		var stock1, stock2 int64
		_ = env.db.QueryRow("SELECT book_stock_quantity FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 1").Scan(&stock1)
		_ = env.db.QueryRow("SELECT book_stock_quantity FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 2").Scan(&stock2)
		w := createPurchase(t, env, acceptanceToken(t, env.router), validPurchaseBody())
		var after1, after2 int64
		_ = env.db.QueryRow("SELECT book_stock_quantity FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 1").Scan(&after1)
		_ = env.db.QueryRow("SELECT book_stock_quantity FROM book_stock WHERE book_stock_store_id = 2 AND book_stock_book_id = 2").Scan(&after2)
		if w.Code < 500 || acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice") != beforeInvoices || acceptanceCount(t, env.db, "SELECT count(*) FROM purchase_invoice_detail") != beforeDetails || acceptanceCount(t, env.db, "SELECT count(*) FROM book_stock_movement") != beforeMovements || after1 != stock1 || after2 != stock2 {
			t.Fatalf("status=%d before=%d/%d after=%d/%d", w.Code, stock1, stock2, after1, after2)
		}
	})
}

func createPurchase(t *testing.T, env *acceptanceEnv, token string, body map[string]interface{}) *httptest.ResponseRecorder {
	t.Helper()
	return acceptanceRequest(t, env.router, http.MethodPost, "/api/purchases/create", token, body)
}

func validPurchaseBody() map[string]interface{} {
	return map[string]interface{}{
		"purchaseInvoiceDate": "2026-02-01", "supplierId": 1, "receivingStoreId": 2,
		"details": []map[string]interface{}{purchaseDetail("0000000000001", 1000, 2), purchaseDetail("0000000000002", 500, 3)},
	}
}

func purchaseBodyWithDetails(count int) map[string]interface{} {
	details := make([]map[string]interface{}, 0, count)
	for i := 1; i <= count; i++ {
		details = append(details, purchaseDetail(formatISBN(i), 100, 1))
	}
	return map[string]interface{}{"purchaseInvoiceDate": "2026-02-01", "supplierId": 1, "receivingStoreId": 2, "details": details}
}

func purchaseDetail(isbn string, price, quantity int64) map[string]interface{} {
	return map[string]interface{}{
		"purchaseInvoiceDetailIsbn": isbn, "purchaseInvoiceDetailUnitPrice": price, "purchaseInvoiceDetailQuantity": quantity,
	}
}

func formatISBN(id int) string {
	return fmt.Sprintf("%013d", id)
}

func assertStockQuantity(t *testing.T, env *acceptanceEnv, storeID, bookID, want int64) {
	t.Helper()
	var quantity int64
	if err := env.db.QueryRow("SELECT book_stock_quantity FROM book_stock WHERE book_stock_store_id = ? AND book_stock_book_id = ?", storeID, bookID).Scan(&quantity); err != nil {
		t.Fatal(err)
	}
	if quantity != want {
		t.Fatalf("stock store=%d book=%d quantity=%d want=%d", storeID, bookID, quantity, want)
	}
}
