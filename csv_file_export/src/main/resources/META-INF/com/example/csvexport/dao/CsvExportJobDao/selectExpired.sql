SELECT * FROM csv_export_jobs
WHERE expires_at IS NOT NULL AND expires_at < /* now */'2026-01-01 00:00:00'
  AND status IN ('COMPLETED', 'FAILED')
