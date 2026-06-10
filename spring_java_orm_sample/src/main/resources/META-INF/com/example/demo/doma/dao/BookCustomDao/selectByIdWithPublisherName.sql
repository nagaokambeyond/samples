select
  b.id,
  b.title,
  b.author,
  b.release_date,
  b.publisher_id,
  p.publisher_name,
  b.update_at,
  b.version
from
  book b
  inner join publisher p on b.publisher_id = p.id
where
  b.id = /* id */1
