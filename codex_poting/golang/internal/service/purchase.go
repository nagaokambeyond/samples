package service

import (
	"context"
	"database/sql"
	"errors"
	"fmt"

	"codex-poting/golang/internal/dbsqlc"
	"codex-poting/golang/internal/problem"
)

func (s *Service) CreatePurchase(ctx context.Context, req PurchaseInvoiceCreateRequest) (PurchaseInvoiceResponse, error) {
	if fields := validatePurchase(req); len(fields) > 0 {
		return PurchaseInvoiceResponse{}, problem.Validation(fields)
	}
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return PurchaseInvoiceResponse{}, err
	}
	defer tx.Rollback()
	q := s.txQueries(tx)
	if ok, err := q.ExistsSupplier(ctx, *req.SupplierID); err != nil {
		return PurchaseInvoiceResponse{}, err
	} else if !ok {
		return PurchaseInvoiceResponse{}, problem.DataValidation(fmt.Sprintf("参照先データが存在しません: supplier(id=%d)", *req.SupplierID))
	}
	if ok, err := q.ExistsStore(ctx, *req.ReceivingStoreID); err != nil {
		return PurchaseInvoiceResponse{}, err
	} else if !ok {
		return PurchaseInvoiceResponse{}, problem.DataValidation(fmt.Sprintf("参照先データが存在しません: store(id=%d)", *req.ReceivingStoreID))
	}
	bookIDs := make([]int64, len(req.Details))
	total := int64(0)
	for i, d := range req.Details {
		id, err := q.FindBookByISBN(ctx, *d.PurchaseInvoiceDetailIsbn)
		if errors.Is(err, sql.ErrNoRows) {
			return PurchaseInvoiceResponse{}, problem.DataValidation(fmt.Sprintf("参照先データが存在しません: book(isbn=%s)", *d.PurchaseInvoiceDetailIsbn))
		}
		if err != nil {
			return PurchaseInvoiceResponse{}, err
		}
		bookIDs[i] = id
		total += *d.PurchaseInvoiceDetailUnitPrice * *d.PurchaseInvoiceDetailQuantity
	}
	now := nowString()
	invoiceID, err := q.CreatePurchaseInvoice(ctx, dbsqlc.CreatePurchaseInvoiceParams{
		PurchaseInvoiceDate: *req.PurchaseInvoiceDate, SupplierID: *req.SupplierID, ReceivingStoreID: *req.ReceivingStoreID,
		PurchaseInvoiceAmount: total, Now: now,
	})
	if err != nil {
		return PurchaseInvoiceResponse{}, err
	}
	for i, d := range req.Details {
		amount := *d.PurchaseInvoiceDetailUnitPrice * *d.PurchaseInvoiceDetailQuantity
		detailID, err := q.CreatePurchaseInvoiceDetail(ctx, dbsqlc.CreatePurchaseInvoiceDetailParams{
			PurchaseInvoiceID: invoiceID, BookID: bookIDs[i], UnitPrice: *d.PurchaseInvoiceDetailUnitPrice, Quantity: *d.PurchaseInvoiceDetailQuantity, Amount: amount, Now: now,
		})
		if err != nil {
			return PurchaseInvoiceResponse{}, err
		}
		if _, err := q.CreateBookStockMovement(ctx, dbsqlc.CreateBookStockMovementParams{
			StoreID: *req.ReceivingStoreID, BookID: bookIDs[i], QuantityDelta: *d.PurchaseInvoiceDetailQuantity,
			SourceID: sql.NullInt64{Int64: invoiceID, Valid: true}, SourceDetailID: sql.NullInt64{Int64: detailID, Valid: true},
			MovementDate: *req.PurchaseInvoiceDate, Now: now,
		}); err != nil {
			return PurchaseInvoiceResponse{}, err
		}
		stock, err := q.GetBookStock(ctx, dbsqlc.GetBookStockParams{StoreID: *req.ReceivingStoreID, BookID: bookIDs[i]})
		if errors.Is(err, sql.ErrNoRows) {
			_, err = q.CreateBookStock(ctx, dbsqlc.CreateBookStockParams{StoreID: *req.ReceivingStoreID, BookID: bookIDs[i], Quantity: *d.PurchaseInvoiceDetailQuantity, Now: now})
		} else if err == nil {
			_, err = q.AddBookStockQuantity(ctx, dbsqlc.AddBookStockQuantityParams{ID: stock.ID, QuantityDelta: *d.PurchaseInvoiceDetailQuantity, Now: now})
		}
		if err != nil {
			return PurchaseInvoiceResponse{}, err
		}
	}
	if err := tx.Commit(); err != nil {
		return PurchaseInvoiceResponse{}, err
	}
	return s.getPurchase(ctx, invoiceID)
}

func (s *Service) getPurchase(ctx context.Context, id int64) (PurchaseInvoiceResponse, error) {
	row, err := s.q.GetPurchaseInvoice(ctx, id)
	if err != nil {
		return PurchaseInvoiceResponse{}, err
	}
	details, err := s.q.ListPurchaseInvoiceDetails(ctx, id)
	if err != nil {
		return PurchaseInvoiceResponse{}, err
	}
	outDetails := make([]PurchaseInvoiceDetailResponse, 0, len(details))
	for _, d := range details {
		outDetails = append(outDetails, PurchaseInvoiceDetailResponse{
			ID: d.ID, PurchaseInvoiceID: d.PurchaseInvoiceID, PurchaseInvoiceDetailBookID: d.PurchaseInvoiceDetailBookID,
			PurchaseInvoiceDetailUnitPrice: d.PurchaseInvoiceDetailUnitPrice, PurchaseInvoiceDetailQuantity: d.PurchaseInvoiceDetailQuantity,
			PurchaseInvoiceDetailAmount: d.PurchaseInvoiceDetailAmount, UpdateAt: d.UpdateAt, Version: d.Version,
		})
	}
	var returnID *int64
	if row.ReturnPurchaseInvoiceID.Valid {
		returnID = &row.ReturnPurchaseInvoiceID.Int64
	}
	invoiceType := "PURCHASE"
	if row.PurchaseInvoiceType == 2 {
		invoiceType = "RETURN_PURCHASE"
	}
	return PurchaseInvoiceResponse{
		ID: row.ID, PurchaseInvoiceType: invoiceType, ReturnPurchaseInvoiceID: returnID, PurchaseInvoiceDate: row.PurchaseInvoiceDate,
		SupplierID: row.SupplierID, ReceivingStoreID: row.ReceivingStoreID, PurchaseInvoiceAmount: row.PurchaseInvoiceAmount,
		UpdateAt: row.UpdateAt, Version: row.Version, Detail: outDetails,
	}, nil
}
