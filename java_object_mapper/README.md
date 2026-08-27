# ModelMapper vs MapStruct Spring Batch + Doma3

Spring Batch 上で同じ DB データを `ModelMapper` と `MapStruct` で変換し、処理時間と throughput を比較するサンプルです。

## Stack

- Java 25
- Spring Boot 4.1.1
- Spring Batch 6.0.5
- Doma3 3.14.0
- Doma Spring Boot Starter 3.0.0
- ModelMapper 3.2.6
- MapStruct 1.6.3
- Lombok 1.18.46
- Lombok MapStruct Binding 0.2.0
- GraalVM Native Build Tools 1.1.8
- H2

## Run

```bash
./gradlew bootRun --args='recordCount=100000'
```

`recordCount` を省略した場合は `100000` 件で実行します。

```bash
./gradlew bootRun --args='recordCount=500000'
```

ログには次のような比較結果が出ます。

```text
ModelMapper result: processed=100000 elapsedMillis=... recordsPerSecond=...
MapStruct result: processed=100000 elapsedMillis=... recordsPerSecond=...
ModelMapper + Lombok DTO result: processed=100000 elapsedMillis=... recordsPerSecond=...
MapStruct + Lombok DTO result: processed=100000 elapsedMillis=... recordsPerSecond=...
```

## 実行結果例

同じ `recordCount=10000` で JVM 実行と GraalVM native 実行を比較した例です。実行環境や実行順、JIT の状態によって数値は変動します。

指標の意味:

- `elapsedMillis`: 各 Spring Batch ジョブ内で、Doma による読み込み、Mapper による変換、Doma による書き込みにかかった処理時間です。単位はミリ秒です。
- `recordsPerSecond`: 1秒あたりに処理できたレコード数です。値が大きいほど throughput が高いことを示します。

実行環境:

- Java: GraalVM 25.0.4
- Spring Boot: 4.1.1
- Spring Batch: 6.0.5
- DB: H2 in-memory
- 実行日時: 2026-08-27

JVM 実行:

```bash
./gradlew bootRun --args='recordCount=10000'
```

| Mapper | processed | elapsedMillis | recordsPerSecond |
| --- | ---: | ---: | ---: |
| ModelMapper | 10000 | 191.881 | 52115.59 |
| MapStruct | 10000 | 155.293 | 64394.37 |
| ModelMapper + Lombok DTO | 10000 | 150.989 | 66229.79 |
| MapStruct + Lombok DTO | 10000 | 144.545 | 69182.77 |

GraalVM native 実行:

```bash
./build/native/nativeCompile/java-object-mapper recordCount=10000
```

| Mapper | processed | elapsedMillis | recordsPerSecond |
| --- | ---: | ---: | ---: |
| ModelMapper | 10000 | 84.860 | 117840.63 |
| MapStruct | 10000 | 64.901 | 154081.13 |
| ModelMapper + Lombok DTO | 10000 | 68.571 | 145833.98 |
| MapStruct + Lombok DTO | 10000 | 63.319 | 157930.69 |

同じ環境で `recordCount=1000000` を指定した実行例です。

JVM 実行:

```bash
./gradlew bootRun --args='recordCount=1000000'
```

| Mapper | processed | elapsedMillis | recordsPerSecond |
| --- | ---: | ---: | ---: |
| ModelMapper | 1000000 | 87320.770 | 11452.03 |
| MapStruct | 1000000 | 86988.247 | 11495.81 |
| ModelMapper + Lombok DTO | 1000000 | 87743.119 | 11396.91 |
| MapStruct + Lombok DTO | 1000000 | 86897.189 | 11507.85 |

GraalVM native 実行:

```bash
./build/native/nativeCompile/java-object-mapper recordCount=1000000
```

| Mapper | processed | elapsedMillis | recordsPerSecond |
| --- | ---: | ---: | ---: |
| ModelMapper | 1000000 | 36487.415 | 27406.71 |
| MapStruct | 1000000 | 38765.006 | 25796.46 |
| ModelMapper + Lombok DTO | 1000000 | 40855.449 | 24476.54 |
| MapStruct + Lombok DTO | 1000000 | 41616.711 | 24028.81 |

## Test

```bash
./gradlew test
```

テストでは Doma DAO の読み書きと、ModelMapper/MapStruct が同じ変換結果を返すことを検証します。

## Lombok Case

Lombok を使うケースでは、Doma-Gen 生成 Entity はそのまま DB 書き込み用に使い、マッピング途中の DTO として `LombokMappedPerson` を使います。

- `ModelMapper + Lombok DTO`: `SourcePeople` から Lombok DTO に変換し、Doma-Gen 生成 Entity へ詰め替えて保存
- `MapStruct + Lombok DTO`: `SourcePeople` から Lombok DTO に変換し、Doma-Gen 生成 Entity へ詰め替えて保存

MapStruct と Lombok の annotation processor 連携には `lombok-mapstruct-binding` を使用しています。

## Native Run

GraalVM 25+ と `native-image` が利用できる環境では native executable をビルドできます。

```bash
./gradlew nativeCompile
```

生成された native executable は次のように実行できます。

```bash
./build/native/nativeCompile/java-object-mapper recordCount=10000
```

Gradle 経由で実行する場合は次のコマンドも使えます。

```bash
./gradlew nativeRun --args='recordCount=10000'
```

Java 25 の native 実行では、ModelMapper 内部依存由来の `sun.misc.Unsafe` 警告と、Spring JDBC の `sql-error-codes.xml` に関する info ログが出ることがあります。比較ジョブ自体はこのログに影響されず完走します。

## Generate Doma Entities

テーブル Entity は Doma-Gen で `src/main/resources/schema.sql` から生成しています。生成物は手書きコードと分かれるように `src/main/generated-doma/java` へ出力します。

```bash
./gradlew generateDomaEntities
```

このタスクは `build/doma-gen/mapperdb` に H2 DB を作成し、`src/main/generated-doma/java/com/example/javaobjectmapper/doma` に次の Entity を再生成します。

- `SourcePeople`
- `MappedPeopleModelmapper`
- `MappedPeopleMapstruct`
- `MappedPeopleLombokModelmapper`
- `MappedPeopleLombokMapstruct`
