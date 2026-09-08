package com.example.resilientapi.job;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(UUID id, String payload, boolean shouldFail, JobStatus status,
                          Instant createdAt, Instant finishedAt) {
    static JobResponse from(Job job) {
        return new JobResponse(job.getId(), job.getPayload(), job.shouldFail(), job.getStatus(),
                job.getCreatedAt(), job.getFinishedAt());
    }
}
