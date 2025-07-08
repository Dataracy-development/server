package com.dataracy.modules.behaviorlog.adapter.message.kafka.test;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.dataracy.modules.common.support.enums.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BehaviorLogProducerTest {

    private final KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    public void sendTestLog() {
        BehaviorLog log = BehaviorLog.builder()
                .userId("124")
                .anonymousId("anon-abc")
                .path("/test")
                .method("GET")
                .status(HttpMethod.GET)
                .responseTime(120)
                .userAgent("Mozilla/5.0")
                .ip("127.0.0.1")
                .action("click")
                .dbLatency(30)
                .externalLatency(40)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("behavior-logs", log);
    }
}

