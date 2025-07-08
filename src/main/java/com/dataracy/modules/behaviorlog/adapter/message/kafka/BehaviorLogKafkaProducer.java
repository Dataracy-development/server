package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaProducer implements BehaviorLogSendProducerPort {
    private final KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    // 토픽 이름
    @Value("${spring.kafka.behavior-log.topic:behavior-logs}")
    private String topic;

    @Override
    public void send(BehaviorLog behaviorLog) {
        // 키: 익명 ID
        kafkaTemplate.send(topic, behaviorLog.getAnonymousId(), behaviorLog);
        log.debug("[Kafka Producer] 행동 로그 전송됨 - {}", behaviorLog);
    }
}
