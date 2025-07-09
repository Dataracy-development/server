package com.dataracy.modules.behaviorlog.adapter.message.kafka.test;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
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
                .httpMethod(HttpMethod.GET)
                .responseTime(123)
                .userAgent("Mozilla/5.0")
                .ip("127.0.0.1")
                .action(ActionType.CLICK)
                .dbLatency(12345)
                .externalLatency(1234)
                .referrer("행동로그 조회")
                .deviceType(DeviceType.MOBILE)
                .requestId("1234567")
                .sessionId("123456")
                .logType(LogType.ACTION)
                .timestamp(Instant.now())
                .build();

        kafkaTemplate.send("behavior-logs", log);
    }
}
