package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaProducer implements BehaviorLogSendProducerPort {
    private final KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    // 토픽 이름
    private static final String TOPIC = "behavior-logs";

    /**
     * 주어진 BehaviorLog 객체를 Kafka 토픽 "behavior-logs"로 전송합니다.
     *
     * BehaviorLog의 익명 ID를 메시지 키로 사용하여 Kafka에 로그를 발행합니다.
     *
     * @param behaviorLog 전송할 행동 로그 객체
     */
    @Override
    public void send(BehaviorLog behaviorLog) {
        // 키: 익명 ID
        kafkaTemplate.send(TOPIC, behaviorLog.getAnonymousId(), behaviorLog);
        log.debug("[Kafka Producer] 행동 로그 전송됨 - {}", behaviorLog);
    }
}
