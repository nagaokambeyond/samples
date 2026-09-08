package com.example.resilientapi.job;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("worker")
public class JobWorker {
    private static final String GROUP = "job-workers";

    private final StringRedisTemplate redisTemplate;
    private final JobService jobService;
    private final String consumerName;

    public JobWorker(StringRedisTemplate redisTemplate, JobService jobService,
                     @Value("${INSTANCE_ID:worker}") String consumerName) {
        this.redisTemplate = redisTemplate;
        this.jobService = jobService;
        this.consumerName = consumerName;
    }

    @PostConstruct
    void createConsumerGroup() {
        try {
            redisTemplate.opsForStream().createGroup(JobPublisher.STREAM_KEY, ReadOffset.latest(), GROUP);
        } catch (DataIntegrityViolationException ignored) {
            // The group already exists, which is expected after a worker restart.
        }
    }

    @Scheduled(fixedDelayString = "${worker.poll-delay-ms:500}")
    void consumeOne() {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                Consumer.from(GROUP, consumerName),
                StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1)),
                StreamOffset.create(JobPublisher.STREAM_KEY, ReadOffset.lastConsumed()));

        if (records == null) {
            return;
        }
        for (MapRecord<String, Object, Object> record : records) {
            try {
                jobService.process(UUID.fromString(record.getValue().get("jobId").toString()));
            } catch (JobNotFoundException ignored) {
                // The database row was removed. Acknowledge so this message cannot block the stream.
            } finally {
                redisTemplate.opsForStream().acknowledge(JobPublisher.STREAM_KEY, GROUP, record.getId());
            }
        }
    }
}
