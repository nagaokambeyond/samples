package com.example.csvexport.web;

import com.example.csvexport.generated.entity.CsvExportJobs;
import java.time.LocalDateTime;

public record ExportJobResponse(Long id, String status, long recordCount, String errorMessage,
                                LocalDateTime createdAt, LocalDateTime completedAt, boolean downloadable) {
    static ExportJobResponse from(CsvExportJobs job) {
        return new ExportJobResponse(job.id, job.status, job.recordCount == null ? 0 : job.recordCount,
                job.errorMessage, job.createdAt, job.completedAt, "COMPLETED".equals(job.status) && job.filePath != null);
    }
}
