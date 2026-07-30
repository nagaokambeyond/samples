select
  id,
  book_id,
  sales_unit_price,
  effective_from,
  effective_to,
  create_at,
  update_at,
  version
from
  book_sales_unit_price_history
where
  book_id = /* bookId */1
  and effective_from < /* effectiveFrom */'2026-01-01'
order by
  effective_from desc
limit
  1
