select
  count(*)
from
  book
where
  1 = 1
/*%if keyword != null && !keyword.trim().isEmpty() */
  and
  (
    lower(title) like concat(lower(/* keyword */'Spring'), '%')
    or lower(author) like concat(lower(/* keyword */'Spring'), '%')
  )
/*%end*/
/*%if releaseDateFrom != null && releaseDateTo != null*/
  and release_date between /* releaseDateFrom */'2020-01-01' and /* releaseDateTo */'2020-01-01'
/*%end*/
