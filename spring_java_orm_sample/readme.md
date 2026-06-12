# spec
http://localhost:8080/swagger-ui/index.html#/

http://localhost:8080/scalar

# ER図

```mermaid
erDiagram
    publisher ||--o{ book : "publishes"
    book_genre ||--o{ book : "categorizes"

    publisher {
        BIGINT id PK
        VARCHAR publisher_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book_genre {
        BIGINT id PK
        VARCHAR genre_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book {
        BIGINT id PK
        VARCHAR title
        VARCHAR author
        DATE release_date
        BIGINT publisher_id FK
        BIGINT genre_id FK
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }
```

# gradleのタスク

```shell
./gradlew domaCodeGenAll
./gradlew runMyBatisGenerator
./gradlew test
```
