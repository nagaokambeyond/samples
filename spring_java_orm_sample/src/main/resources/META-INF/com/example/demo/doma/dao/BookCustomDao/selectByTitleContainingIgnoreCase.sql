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
  1 = 1
/*%if keyword != null && !keyword.trim().isEmpty() */
  and
  lower(b.title) like concat('%', lower(/* keyword */'Spring'), '%')
/*%end*/
/*%if releaseDateFrom != null && releaseDateTo != null*/
  and b.release_date between /* releaseDateFrom */'2020-01-01' and /* releaseDateTo */'2020-01-01'
/*%end*/
order by
  b.id
limit
  /* limit */10
offset
  /* offset */0
