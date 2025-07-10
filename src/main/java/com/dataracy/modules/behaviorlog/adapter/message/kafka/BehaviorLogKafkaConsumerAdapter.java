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

    @KafkaListener(
            topics = "${spring.kafka.consumer.behavior-log.topic}",
            groupId = "${spring.kafka.consumer.behavior-log.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            log.debug("Kafka 메시지 수신 - 토픽: {}, 파티션: {}, 오프셋: {}",
            record.topic(), record.partition(), record.offset());

            BehaviorLog logData = objectMapper.readValue(message, BehaviorLog.class);
            saveBehaviorLogPort.save(logData);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 실패 - 메시지 무시: {}", record.value(), e);
        } catch (Exception e) {
            log.error("행동 로그 저장 중 오류 발생 - 재시도 가능한 오류일 수 있음", e);
            throw e; // 재시도를 위해 예외를 다시 던짐
        }
    }
}
