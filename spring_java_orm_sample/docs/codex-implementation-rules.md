# Codex 実装時の追加規約

## 基本方針

- 変更は依頼内容に必要な範囲へ限定する。
- 無関係なリファクタリング、命名変更、フォーマット変更は避ける。
- 既存の未コミット変更を勝手に戻さない。
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングは避ける。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にする。
- メソッド内の変数宣言には、`final var` を積極的に使う。
- `LocalDateTime.now()` を頻繁に呼ばず、同じ処理内では必要に応じて値を使い回す。
- record にしたいところでは、Lombok の `@Value` を付けた class にする。
- API のリクエスト、レスポンス項目にある `List` には `@NotNull` を付ける。
- 未使用なメソッドであれば削除する。

## API 実装

- API 仕様を変更する場合は、`BookApi`、`BooksApiController`、request / response DTO、`BookApiControllerValidator`、`GlobalExceptionHandler`、`readme.md` の整合性を確認する。
- OpenAPI 注釈は `BookApi` に集約する。Controller 側へ重複して追加しない。
- API の入出力には Entity ではなく request / response DTO を使う。
- `BookCreateRequest`、`BookUpdateRequest`、`BookResponse` には `releaseDate` と `publisherId` が含まれる。スキーマや永続化層を変更する場合は DTO も確認する。
- 検索 API は `title`、任意の `releaseDateFrom` / `releaseDateTo`、`page`、`size` を扱う。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とし、片方だけの指定や From > To は相関バリデーションエラーとして扱う。
- 日付範囲の相関チェックは `BookApiControllerValidator` に集約する。
- `page` は 0 始まり、`size` は 1 以上とする。API の Bean Validation と各実装の offset 計算の整合性を維持する。
- 検索 API のレスポンスは `BookPageResponse` とする。検索仕様を変更する場合は `content`、`page`、`size`、`totalElements`、`totalPages` の意味を3つの Service 実装で揃える。
- `BookResponse` には `publisherName` が含まれる。取得・検索系の SQL / JPQL は `publisher` と結合し、`BookConverter` に渡す値を揃える。

## Service と例外

- `BookService` は JPA / MyBatis / Doma 共通の Service インターフェースとして扱う。
- Service インターフェースを変更する場合は、JPA / MyBatis / Doma の3実装をすべて確認する。
- DB を読む・更新する Service メソッドには `@Transactional` を付ける。
- `publisherId` は `publisher` への外部キー。登録・更新時の参照存在チェックは各永続化方式の `BookDataValidator*` に集約する。
- 更新・削除処理では、既存のバージョンチェックと書き込みロックを不用意に変更しない。
- 更新競合は `ObjectOptimisticLockingFailureException` と `GlobalExceptionHandler` により HTTP 409 として扱う。
- データなしは `RepositoryDataNotfoundException` と `GlobalExceptionHandler` により HTTP 404 として扱う。
- 相関バリデーションエラーは `CorrelationValidationFailureException` と `GlobalExceptionHandler` により HTTP 400 として扱う。
- 外部キー参照先なしは `ForeignKeyReferenceNotFoundException` と `GlobalExceptionHandler` により HTTP 400 として扱う。

## テスト

コード変更後は、基本的に以下を実行する。

```shell
./gradlew test
```

対象が限定されている場合でも、関連するテストを確認する。

- API 相関バリデーション: `BookApiControllerValidatorTest`
- API Controller: `BooksApiControllerTest`
- 例外ハンドリング: `GlobalExceptionHandlerTest`
- JPA 実装: `BookServiceJPATest`
- MyBatis 実装: `BookServiceMybatisTest`
- Doma 実装: `BookServiceDomaTest`
- Doma 生成 DAO: `BookDaoTest`
- Doma 生成 Publisher DAO: `PublisherDaoTest`
- 行ロック関連: `BookRowLock`

API、Security、DB 設定、JPA / MyBatis / Doma の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認する。
