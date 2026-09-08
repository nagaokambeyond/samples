package com.example.resilientapi.job;

import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {
    private final JobRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public JobService(JobRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public JobResponse create(JobRequest request) {
        Job job = repository.save(Job.pending(request.payload(), request.shouldFail()));
        eventPublisher.publishEvent(new JobCreatedEvent(job.getId()));
        return JobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public JobResponse find(UUID id) {
        return repository.findById(id).map(JobResponse::from)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(JobResponse::from).toList();
    }

    @Transactional
    public void process(UUID id) {
        Job job = repository.findById(id).orElseThrow(() -> new JobNotFoundException(id));
        if (job.shouldFail()) {
            job.fail();
            return;
        }
        job.complete();
    }
}
