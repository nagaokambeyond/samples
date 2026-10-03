package com.example.csvexport.service;

import com.example.csvexport.generated.entity.CsvExportJobs;
import com.example.csvexport.model.JobStatus;
import com.example.csvexport.storage.FileStorage;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.csvexport.dao.CsvExportJobDao;

@Component
public class ExportDispatcher {
    private final ExportJobService jobs; private final CsvZipExporter exporter; private final CsvExportJobDao dao; private final FileStorage storage;
    public ExportDispatcher(ExportJobService jobs, CsvZipExporter exporter, CsvExportJobDao dao, FileStorage storage) { this.jobs = jobs; this.exporter = exporter; this.dao = dao; this.storage = storage; }
    @Scheduled(fixedDelayString = "${app.export.poll-delay-ms}")
    public void dispatch() { CsvExportJobs job = jobs.claimNext(); if (job != null) exporter.submit(job); }
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanup() {
        for (CsvExportJobs job : dao.selectExpired(LocalDateTime.now())) {
            try { if (job.filePath != null) storage.delete(job.filePath); } catch (Exception ignored) { continue; }
            job.status = JobStatus.EXPIRED.name(); dao.delete(job);
        }
    }
}
