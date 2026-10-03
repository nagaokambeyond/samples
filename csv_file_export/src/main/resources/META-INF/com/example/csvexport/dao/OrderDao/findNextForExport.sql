SELECT * FROM orders
WHERE created_at <= /* snapshotAt */'2026-01-01 00:00:00'
/*%if filter.status != null && !filter.status.isBlank() */ AND status = /* filter.status */'NEW' /*%end*/
/*%if filter.orderDateFrom != null */ AND ordered_at >= /* filter.orderDateFrom */'2026-01-01' /*%end*/
/*%if filter.orderDateTo != null */ AND ordered_at < DATEADD('DAY', 1, CAST(/* filter.orderDateTo */'2026-01-01' AS DATE)) /*%end*/
/*%if filter.customerName != null && !filter.customerName.isBlank() */ AND customer_name LIKE /* @infix(filter.customerName) */'x' /*%end*/
/*%if filter.amountMin != null */ AND amount >= /* filter.amountMin */0 /*%end*/
/*%if filter.amountMax != null */ AND amount <= /* filter.amountMax */0 /*%end*/
/*%if afterOrderedAt != null */
  AND (ordered_at > /* afterOrderedAt */'2026-01-01 00:00:00'
       OR (ordered_at = /* afterOrderedAt */'2026-01-01 00:00:00' AND id > /* afterId */0))
/*%end*/
ORDER BY ordered_at ASC, id ASC LIMIT /* limit */1000
