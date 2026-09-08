package com.example.resilientapi.job;

import java.util.Map;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class JobPublisher {
    public static final String STREAM_KEY = "jobs";

    private final StringRedisTemplate redisTemplate;

    public JobPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(JobCreatedEvent event) {
        redisTemplate.opsForStream().add(MapRecord.create(STREAM_KEY,
                Map.of("jobId", event.jobId().toString())));
    }
}
