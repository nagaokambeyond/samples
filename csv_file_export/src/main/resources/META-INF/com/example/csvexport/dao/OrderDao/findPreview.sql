SELECT * FROM orders
WHERE 1 = 1
/*%if filter.status != null && !filter.status.isBlank() */ AND status = /* filter.status */'NEW' /*%end*/
/*%if filter.orderDateFrom != null */ AND ordered_at >= /* filter.orderDateFrom */'2026-01-01' /*%end*/
/*%if filter.orderDateTo != null */ AND ordered_at < DATEADD('DAY', 1, CAST(/* filter.orderDateTo */'2026-01-01' AS DATE)) /*%end*/
/*%if filter.customerName != null && !filter.customerName.isBlank() */ AND customer_name LIKE /* @infix(filter.customerName) */'x' /*%end*/
/*%if filter.amountMin != null */ AND amount >= /* filter.amountMin */0 /*%end*/
/*%if filter.amountMax != null */ AND amount <= /* filter.amountMax */0 /*%end*/
ORDER BY ordered_at DESC, id DESC LIMIT 100
