package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.application.port.out.SaveBehaviorLogPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final SaveBehaviorLogPort saveBehaviorLogPort;

    /**
     * Kafka에서 행동 로그 메시지를 수신하여 역직렬화, 유효성 검사 후 저장합니다.
     *
     * 메시지가 유효하지 않거나 JSON 역직렬화에 실패한 경우 해당 메시지는 무시됩니다.
     * 저장 중 예외가 발생하면 예외를 다시 던져 Kafka의 재시도 메커니즘이 동작하도록 합니다.
     *
     * @param record Kafka에서 수신한 행동 로그 메시지 레코드
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.behavior-log.topic}",
            groupId = "${spring.kafka.consumer.behavior-log.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        String message = record.value();
        log.debug("Kafka 수신 - topic={}, offset={}", record.topic(), record.offset());

        try {
            BehaviorLog logData = objectMapper.readValue(message, BehaviorLog.class);
            saveBehaviorLogPort.save(logData);
        } catch (JsonProcessingException e) {
            log.warn("Kafka 메시지 역직렬화 실패 - 무시됨: {}", message, e);
        } catch (Exception e) {
            log.error("행동 로그 저장 중 오류 발생", e);
            throw e; // Spring-Kafka가 retry 하도록
        }
    }
}
