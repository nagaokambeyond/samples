# swagger-ui
http://localhost:8080/swagger-ui/index.html#/

# リクエストサンプル

```shell
curl -X GET "http://localhost:8080/api/books" 
```

```shell
curl -X GET "http://localhost:8080/api/books/1" 
```

```shell
curl -X POST "http://localhost:8080/api/books/create" \
-H "Content-Type: application/json" \
-d '{
"title": "あああ入門",
"author": "いいい太郎"
}'
```

```shell
curl -X POST "http://localhost:8080/api/books/update" \
-H "Content-Type: application/json" \
-d '{
"id":1 ,
"title": "かかか入門",
"author": "ききき太郎"
}'
```

```shell
curl -X DELETE "http://localhost:8080/api/books/1" 
```
