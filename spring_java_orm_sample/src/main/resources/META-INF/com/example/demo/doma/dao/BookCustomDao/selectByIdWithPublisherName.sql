select
  b.id as b_id,
  b.title as b_title,
  b.author as b_author,
  b.release_date as b_release_date,
  b.publisher_id as b_publisher_id,
  p.publisher_name as b_publisher_name,
  b.genre_id as b_genre_id,
  g.genre_name as b_genre_name,
  b.isbn as b_isbn,
  b.update_at as b_update_at,
  b.version as b_version,
  bs.id as bs_id,
  bs.book_stock_store_id as bs_book_stock_store_id,
  s.store_name as bs_store_name,
  bs.book_stock_quantity as bs_book_stock_quantity
from
  book b
  inner join publisher p on b.publisher_id = p.id
  inner join book_genre g on b.genre_id = g.id
  left join book_stock bs on bs.book_stock_book_id = b.id
  left join store s on s.id = bs.book_stock_store_id
where
  b.id = /* id */1
order by
  b.id,
  bs.id
