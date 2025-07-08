package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final SaveBehaviorLogPort saveBehaviorLogPort;

    @KafkaListener(topics = "${spring.kafka.consumer.behavior-log.topic}", groupId = "${spring.kafka.consumer.behavior-log.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            log.debug("Kafka 수신 로그: {}", message);

            BehaviorLog logData = objectMapper.readValue(message, BehaviorLog.class);
            // 필수 필드 검증
            if (logData.getUserId() == null && logData.getAnonymousId() == null) {
                log.warn("사용자 식별 정보가 없는 로그 메시지 무시: {}", message);
                return;
            }
            saveBehaviorLogPort.save(logData);
        } catch (Exception e) {
            log.error("Kafka 행동 로그 소비 중 오류 발생", e);
        }
    }
}
