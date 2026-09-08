package com.example.resilientapi.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String payload;

    @Column(nullable = false)
    private boolean shouldFail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant finishedAt;

    protected Job() {
    }

    private Job(String payload, boolean shouldFail) {
        this.payload = payload;
        this.shouldFail = shouldFail;
        this.status = JobStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public static Job pending(String payload, boolean shouldFail) {
        return new Job(payload, shouldFail);
    }

    public void complete() {
        this.status = JobStatus.COMPLETED;
        this.finishedAt = Instant.now();
    }

    public void fail() {
        this.status = JobStatus.FAILED;
        this.finishedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public boolean shouldFail() {
        return shouldFail;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }
}
