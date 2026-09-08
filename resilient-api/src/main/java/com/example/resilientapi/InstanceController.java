package com.example.resilientapi;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {
    private final String instanceId;

    public InstanceController(@Value("${INSTANCE_ID:local}") String instanceId) {
        this.instanceId = instanceId;
    }

    @GetMapping("/api/instance")
    public Map<String, String> instance() {
        return Map.of("instanceId", instanceId);
    }
}
