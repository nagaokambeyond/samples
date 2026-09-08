package com.example.resilientapi.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    private JobRepository repository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private JobService service;

    @Test
    void createsPendingJobAndPublishesIt() {
        when(repository.save(org.mockito.ArgumentMatchers.any(Job.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(new JobRequest("send email", false));

        verify(eventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(JobCreatedEvent.class));
    }

    @Test
    void marksFailingJobAsFailed() {
        UUID id = UUID.randomUUID();
        Job job = Job.pending("fail", true);
        when(repository.findById(id)).thenReturn(Optional.of(job));

        service.process(id);

        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
    }
}
