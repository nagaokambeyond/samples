select
  b.id,
  b.title,
  b.author,
  b.release_date,
  b.publisher_id,
  p.publisher_name,
  b.genre_id,
  g.genre_name,
  b.update_at,
  b.version
from
  book b
  inner join publisher p on b.publisher_id = p.id
  inner join book_genre g on b.genre_id = g.id
where
  b.id = /* id */1
