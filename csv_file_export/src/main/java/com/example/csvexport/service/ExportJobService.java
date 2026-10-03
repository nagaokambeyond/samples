package com.example.csvexport.service;

import com.example.csvexport.dao.CsvExportJobDao;
import com.example.csvexport.generated.entity.CsvExportJobs;
import com.example.csvexport.model.JobStatus;
import com.example.csvexport.model.OrderFilter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExportJobService {
    private final CsvExportJobDao jobs;
    private final ObjectMapper objectMapper;
    public ExportJobService(CsvExportJobDao jobs, ObjectMapper objectMapper) { this.jobs = jobs; this.objectMapper = objectMapper; }

    @Transactional
    public CsvExportJobs create(String owner, OrderFilter filter) {
        var job = new CsvExportJobs();
        job.ownerUsername = owner;
        job.status = JobStatus.QUEUED.name();
        job.filterJson = write(filter);
        job.recordCount = 0L;
        job.createdAt = LocalDateTime.now();
        jobs.insert(job);
        return job;
    }
    @Transactional
    public CsvExportJobs claimNext() {
        CsvExportJobs job = jobs.selectNextQueuedForUpdate();
        if (job == null) return null;
        job.status = JobStatus.RUNNING.name();
        job.snapshotAt = LocalDateTime.now();
        job.startedAt = job.snapshotAt;
        jobs.update(job);
        return job;
    }
    public CsvExportJobs ownedJob(Long id, String owner) { return jobs.selectByIdAndOwner(id, owner); }
    public List<CsvExportJobs> ownedJobs(String owner) { return jobs.selectByOwner(owner); }
    public OrderFilter readFilter(CsvExportJobs job) {
        try { return objectMapper.readValue(job.filterJson, OrderFilter.class); }
        catch (JacksonException e) { throw new IllegalStateException("Invalid stored filter", e); }
    }
    @Transactional
    public void complete(CsvExportJobs job, String path, long count, int retentionHours) {
        job.status = JobStatus.COMPLETED.name(); job.filePath = path; job.recordCount = count;
        job.completedAt = LocalDateTime.now(); job.expiresAt = job.completedAt.plusHours(retentionHours); jobs.update(job);
    }
    @Transactional
    public void fail(CsvExportJobs job, Exception failure, int retentionHours) {
        job.status = JobStatus.FAILED.name(); job.errorMessage = failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage().substring(0, Math.min(1000, failure.getMessage().length()));
        job.completedAt = LocalDateTime.now(); job.expiresAt = job.completedAt.plusHours(retentionHours); jobs.update(job);
    }
    private String write(OrderFilter filter) {
        try { return objectMapper.writeValueAsString(filter); }
        catch (JacksonException e) { throw new IllegalStateException("Could not serialize filter", e); }
    }
}
