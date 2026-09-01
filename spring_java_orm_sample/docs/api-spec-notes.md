# API 仕様メモ

API の OpenAPI 定義と注釈は `AuthOperationApi`、`BooksOperationApi`、`OpenBdBooksApi`、`PurchaseOperationApi` に集約します。API の入出力には Entity ではなく request / response DTO を使用します。

## 認証と認可

- `POST /api/auth/login` は `LoginRequest` を受け取り、`LoginResponse` として `Bearer` token、ユーザー名、有効期限秒数を返します。
- `POST /api/auth/login-rate-limit/reset` は Bearer token 必須で、成功時は 204 を返します。
- ログインの日次回数制限は `app.auth.login-rate-limit` で管理します。
- ログイン、書籍の取得・検索、OpenBD 書誌取得は公開します。
- 書籍の登録・更新・削除、販売単価履歴追加、仕入登録は Bearer token を必要とします。
- `/bootui` と `/bootui/**` は認証対象外です。

## 書籍 API

- 書籍の登録・更新・取得・削除と検索は `/api/books` 配下で提供します。
- `BookCreateRequest`、`BookUpdateRequest`、`BookResponse` は `releaseDate`、`publisherId`、`genreId`、`isbn` を扱います。
- ISBN は `@Isbn` で13桁数字として検証し、登録・更新時は各 `BookDataValidator*` で一意性を確認します。
- `BookCreateRequest` と `BookResponse` は `salesUnitPrice` を含みます。販売単価は履歴管理し、`BookUpdateRequest` では直接変更しません。
- `BookResponse` は `publisherName`、`genreName`、`isbn`、現在の `salesUnitPrice`、`bookStockList` を返します。
- `BookStockResponse` は `id`、`bookStockStoreId`、`storeName`、`bookStockQuantity` を返します。

### 検索

- 任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱います。
- `keyword` はタイトルまたは著者の前方一致です。未指定または空文字の場合はタイトル・著者条件を付けません。
- `releaseDateFrom` / `releaseDateTo` は両方指定または両方未指定とし、片方だけの指定や From > To は相関バリデーションエラーです。
- `page` は0始まりです。
- ページサイズは `application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込みます。
- レスポンスは `BookPageResponse` とし、`content`、`page`、`size`、`totalElements`、`totalPages` を返します。

### 販売単価履歴

- `POST /api/books/{id}/sales-unit-prices` は `BookSalesUnitPriceCreateRequest` で `salesUnitPrice` と未来日の `effectiveFrom` を受け取り、空 body の 200 を返します。
- 同じ `book_id,effective_from` の履歴は一意制約違反として扱います。
- 追加時は前履歴の `effective_to` を新履歴の前日にし、後続履歴があれば新履歴の `effective_to` を後続履歴の前日にします。
- 現在単価は `effective_from <= current_date` かつ `effective_to IS NULL OR current_date <= effective_to` の履歴です。

## OpenBD API

- `GET /api/books/openbd` は必須の `isbn` query parameter を受け取ります。
- `isbn` は13桁 ISBN またはカンマ区切りの13桁 ISBN として検証します。
- OpenAPI Generator 生成の `BooksApi#getBooksByIsbn(isbn, null)` を呼び、生成 DTO ではなく `OpenBdBookResponse` のリストを返します。
- レスポンスに `null` の書誌が含まれる場合は書誌なしとして扱います。

## 仕入 API

- `POST /api/purchases/create` は `PurchaseInvoiceCreateRequest` を受け取り、`PurchaseInvoiceResponse` を返します。
- request は `purchaseInvoiceDate`、`supplierId`、`receivingStoreId`、明細リストを扱います。
- `details` は `@Valid`、`@NotEmpty`、`@NotNull`、`@Size(max = 10)` を適用します。
- 明細の単価は1〜10000、数量は1〜1000です。
- 明細は `purchaseInvoiceDetailIsbn` で本を参照します。
- `supplierId`、`receivingStoreId`、明細 ISBN の存在を確認し、ISBN から本 ID を解決します。
- 明細金額と伝票金額を計算し、伝票、明細、在庫、在庫増減履歴を同一トランザクションで更新します。
- 在庫増減履歴は `PURCHASE` / `PURCHASE_INVOICE` として登録します。
- response は伝票金額、更新日時、バージョン、`PurchaseInvoiceDetailResponse` の明細リストを返します。

## ProblemDetail

`GlobalExceptionHandler` は次を ProblemDetail に変換します。

- Bean Validation の `ConstraintViolationException`、相関バリデーションの `CorrelationValidationFailureException`: 400
- `ForeignKeyReferenceNotFoundException`: 400
- ISBN または販売単価履歴の `UniqueConstraintValidationException`: 400
- 認証エラー: 401
- `RepositoryDataNotfoundException`、`OpenBdBookNotFoundException`: 404
- `ObjectOptimisticLockingFailureException`、`PessimisticLockingFailureException`: 409
- `LoginRateLimitExceededException`: 429
- OpenBD 生成クライアントの `ApiException`: 502

validation error の `field` / `message` 形式は `ExceptionHandlerUtil` で組み立てます。
