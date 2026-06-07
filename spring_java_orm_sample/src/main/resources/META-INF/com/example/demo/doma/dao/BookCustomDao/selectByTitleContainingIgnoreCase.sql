select
  /*%expand*/*
from
  book
where
  lower(title) like concat('%', lower(/* keyword */'Spring'), '%')
/*%if releaseDateFrom != null && releaseDateTo != null*/
  and release_date between /* releaseDateFrom */'2020-01-01' and /* releaseDateTo */'2020-01-01'
/*%end*/
order by
  id
