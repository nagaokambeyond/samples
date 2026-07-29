package service

import (
	"fmt"

	"codex-poting/golang/internal/problem"
)

func validatePurchase(req PurchaseInvoiceCreateRequest) []problem.FieldError {
	var fields []problem.FieldError
	checkDate("purchaseInvoiceDate", req.PurchaseInvoiceDate, &fields)
	checkRequiredInt("supplierId", req.SupplierID, &fields)
	checkRequiredInt("receivingStoreId", req.ReceivingStoreID, &fields)
	if len(req.Details) == 0 || len(req.Details) > 10 {
		fields = append(fields, problem.FieldError{Field: "details", Message: "1 から 10 件にしてください"})
	}
	for i, d := range req.Details {
		prefix := fmt.Sprintf("details[%d].", i)
		checkISBN(prefix+"purchaseInvoiceDetailIsbn", d.PurchaseInvoiceDetailIsbn, &fields)
		checkRange(prefix+"purchaseInvoiceDetailUnitPrice", d.PurchaseInvoiceDetailUnitPrice, 1, 10000, &fields)
		checkRange(prefix+"purchaseInvoiceDetailQuantity", d.PurchaseInvoiceDetailQuantity, 1, 1000, &fields)
	}
	return fields
}
