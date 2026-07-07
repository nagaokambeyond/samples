select
  coalesce(max(id), 0) + 1
from
  book_sales_unit_price_history
