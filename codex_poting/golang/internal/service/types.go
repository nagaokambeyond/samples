package service

type BookStockResponse struct {
	ID                int64  `json:"id"`
	BookStockStoreID  int64  `json:"bookStockStoreId"`
	StoreName         string `json:"storeName"`
	BookStockQuantity int64  `json:"bookStockQuantity"`
}

type BookResponse struct {
	ID             int64               `json:"id"`
	Title          string              `json:"title"`
	Author         *string             `json:"author,omitempty"`
	ReleaseDate    string              `json:"releaseDate"`
	PublisherID    int64               `json:"publisherId"`
	PublisherName  string              `json:"publisherName"`
	GenreID        int64               `json:"genreId"`
	GenreName      string              `json:"genreName"`
	Isbn           string              `json:"isbn"`
	SalesUnitPrice int64               `json:"salesUnitPrice"`
	UpdateAt       string              `json:"updateAt"`
	Version        int64               `json:"version"`
	BookStockList  []BookStockResponse `json:"bookStockList"`
}

type BookPageResponse struct {
	Content       []BookResponse `json:"content"`
	Page          int64          `json:"page"`
	Size          int64          `json:"size"`
	TotalElements int64          `json:"totalElements"`
	TotalPages    int64          `json:"totalPages"`
}

type BookCreateRequest struct {
	Title          *string `json:"title"`
	Author         *string `json:"author"`
	ReleaseDate    *string `json:"releaseDate"`
	PublisherID    *int64  `json:"publisherId"`
	GenreID        *int64  `json:"genreId"`
	Isbn           *string `json:"isbn"`
	SalesUnitPrice *int64  `json:"salesUnitPrice"`
}

type BookUpdateRequest struct {
	ID          *int64  `json:"id"`
	Title       *string `json:"title"`
	Author      *string `json:"author"`
	ReleaseDate *string `json:"releaseDate"`
	PublisherID *int64  `json:"publisherId"`
	GenreID     *int64  `json:"genreId"`
	Isbn        *string `json:"isbn"`
	Version     *int64  `json:"version"`
}

type SalesUnitPriceCreateRequest struct {
	SalesUnitPrice *int64  `json:"salesUnitPrice"`
	EffectiveFrom  *string `json:"effectiveFrom"`
}

type PurchaseInvoiceCreateRequest struct {
	PurchaseInvoiceDate *string                              `json:"purchaseInvoiceDate"`
	SupplierID          *int64                               `json:"supplierId"`
	ReceivingStoreID    *int64                               `json:"receivingStoreId"`
	Details             []PurchaseInvoiceDetailCreateRequest `json:"details"`
}

type PurchaseInvoiceDetailCreateRequest struct {
	PurchaseInvoiceDetailIsbn      *string `json:"purchaseInvoiceDetailIsbn"`
	PurchaseInvoiceDetailUnitPrice *int64  `json:"purchaseInvoiceDetailUnitPrice"`
	PurchaseInvoiceDetailQuantity  *int64  `json:"purchaseInvoiceDetailQuantity"`
}

type PurchaseInvoiceResponse struct {
	ID                      int64                           `json:"id"`
	PurchaseInvoiceType     string                          `json:"purchaseInvoiceType"`
	ReturnPurchaseInvoiceID *int64                          `json:"returnPurchaseInvoiceId"`
	PurchaseInvoiceDate     string                          `json:"purchaseInvoiceDate"`
	SupplierID              int64                           `json:"supplierId"`
	ReceivingStoreID        int64                           `json:"receivingStoreId"`
	PurchaseInvoiceAmount   int64                           `json:"purchaseInvoiceAmount"`
	UpdateAt                string                          `json:"updateAt"`
	Version                 int64                           `json:"version"`
	Detail                  []PurchaseInvoiceDetailResponse `json:"detail"`
}

type PurchaseInvoiceDetailResponse struct {
	ID                             int64  `json:"id"`
	PurchaseInvoiceID              int64  `json:"purchaseInvoiceId"`
	PurchaseInvoiceDetailBookID    int64  `json:"purchaseInvoiceDetailBookId"`
	PurchaseInvoiceDetailUnitPrice int64  `json:"purchaseInvoiceDetailUnitPrice"`
	PurchaseInvoiceDetailQuantity  int64  `json:"purchaseInvoiceDetailQuantity"`
	PurchaseInvoiceDetailAmount    int64  `json:"purchaseInvoiceDetailAmount"`
	UpdateAt                       string `json:"updateAt"`
	Version                        int64  `json:"version"`
}
