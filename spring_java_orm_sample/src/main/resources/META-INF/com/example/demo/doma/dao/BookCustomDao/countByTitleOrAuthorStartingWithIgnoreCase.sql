select
  count(*)
from
  book b
  inner join book_sales_unit_price_history h
    on h.book_id = b.id
    and h.effective_from <= current_date
    and (h.effective_to is null or current_date <= h.effective_to)
where
  1 = 1
/*%if keyword != null && !keyword.trim().isEmpty() */
  and
  (
    lower(b.title) like concat(lower(/* keyword */'Spring'), '%')
    or lower(b.author) like concat(lower(/* keyword */'Spring'), '%')
  )
/*%end*/
/*%if releaseDateFrom != null && releaseDateTo != null*/
  and b.release_date between /* releaseDateFrom */'2020-01-01' and /* releaseDateTo */'2020-01-01'
/*%end*/
