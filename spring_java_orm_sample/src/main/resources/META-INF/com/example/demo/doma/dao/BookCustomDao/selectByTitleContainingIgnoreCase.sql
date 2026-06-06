select
  /*%expand*/*
from
  book
where
  lower(title) like concat('%', lower(/* keyword */'Spring'), '%')
order by
  id
