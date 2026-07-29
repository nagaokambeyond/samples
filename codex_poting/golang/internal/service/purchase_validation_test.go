package service

import (
	"testing"

	"codex-poting/golang/internal/problem"
)

func TestValidatePurchaseRequestRequiredFields(t *testing.T) {
	fields := validatePurchase(PurchaseInvoiceCreateRequest{})

	for _, field := range []string{"purchaseInvoiceDate", "supplierId", "receivingStoreId", "details"} {
		if !hasPurchaseFieldError(fields, field) {
			t.Fatalf("expected field %q in errors: %+v", field, fields)
		}
	}
}

func TestValidatePurchaseRequestDetailFields(t *testing.T) {
	date := "2026-02-01"
	supplierID := int64(1)
	storeID := int64(2)
	isbn := "invalid"
	unitPrice := int64(0)
	quantity := int64(1001)

	fields := validatePurchase(PurchaseInvoiceCreateRequest{
		PurchaseInvoiceDate: &date,
		SupplierID:          &supplierID,
		ReceivingStoreID:    &storeID,
		Details: []PurchaseInvoiceDetailCreateRequest{
			{
				PurchaseInvoiceDetailIsbn:      &isbn,
				PurchaseInvoiceDetailUnitPrice: &unitPrice,
				PurchaseInvoiceDetailQuantity:  &quantity,
			},
		},
	})

	for _, field := range []string{
		"details[0].purchaseInvoiceDetailIsbn",
		"details[0].purchaseInvoiceDetailUnitPrice",
		"details[0].purchaseInvoiceDetailQuantity",
	} {
		if !hasPurchaseFieldError(fields, field) {
			t.Fatalf("expected field %q in errors: %+v", field, fields)
		}
	}
}

func hasPurchaseFieldError(fields []problem.FieldError, field string) bool {
	for _, item := range fields {
		if item.Field == field {
			return true
		}
	}
	return false
}
