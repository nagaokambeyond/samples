insert into book_sales_unit_price_history (
  id,
  book_id,
  sales_unit_price,
  effective_from,
  effective_to,
  create_at,
  update_at,
  version
) values (
  /* entity.id */1,
  /* entity.bookId */1,
  /* entity.salesUnitPrice */1200,
  /* entity.effectiveFrom */'2026-01-01',
  /* entity.effectiveTo */'2026-12-31',
  /* entity.createAt */'2026-01-01 00:00:00',
  /* entity.updateAt */'2026-01-01 00:00:00',
  /* entity.version */1
)
