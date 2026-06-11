# spec
http://localhost:8080/swagger-ui/index.html#/

http://localhost:8080/scalar

# gradleのタスク

```shell
./gradlew domaCodeGenAll
./gradlew runMyBatisGenerator
./gradlew test
```

# リクエストサンプル

```shell
curl -X GET "http://localhost:8080/api/books" 
```

```shell
curl -X GET "http://localhost:8080/api/books/1" 
```

```shell
curl -X GET "http://localhost:8080/api/books/search?title=Spring&page=0"
```

```shell
curl -X POST "http://localhost:8080/api/books/create" \
-H "Content-Type: application/json" \
-d '{
"title": "あああ入門",
"author": "いいい太郎",
"releaseDate": "2021-01-01",
"publisherId": 1
}'
```

```shell
curl -X POST "http://localhost:8080/api/books/update" \
-H "Content-Type: application/json" \
-d '{
"id":1 ,
"title": "かかか入門",
"author": "ききき太郎",
"releaseDate": "2021-02-01",
"publisherId": 1,
"version": 0
}'
```

```shell
curl -X POST "http://localhost:8080/api/books/update" \
-H "Content-Type: application/json" \
-d '{
"id":"a" ,
"title": "かかか入門",
"author": "ききき太郎",
"releaseDate": "2021-02-01",
"publisherId": 1,
"version": 0
}'
```

```shell
curl -X POST "http://localhost:8080/api/books/update" \
-H "Content-Type: application/json" \
-d '{
"id":9999 ,
"title": "かかか入門",
"author": "ききき太郎",
"releaseDate": "2021-02-01",
"publisherId": 1,
"version": 0
}'
```


```shell
curl -X DELETE "http://localhost:8080/api/books/1" 
```
