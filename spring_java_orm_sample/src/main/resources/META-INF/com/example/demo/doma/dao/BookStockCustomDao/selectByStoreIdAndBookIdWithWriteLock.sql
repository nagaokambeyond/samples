select
  /*%expand*/*
from
  book_stock
where
  book_stock_store_id = /* bookStockStoreId */1
  and book_stock_book_id = /* bookStockBookId */1
for update nowait
